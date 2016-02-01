package org.cyclops.fluidconverters.fluidgroup;

import com.google.gson.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;
import org.cyclops.fluidconverters.FluidConverters;
import org.cyclops.fluidconverters.Reference;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Loads all fluid groups into instance of FluidGroup in a given directory.
 * @author immortaleeb
 */
public class FluidGroupsLoader {

    private File configDirectory;
    private JsonParser parser;

    /**
     * Creates a new instance
     * @param configDirectory The config directory of this mod
     */
    public FluidGroupsLoader(File configDirectory) throws IOException {
        this.configDirectory = configDirectory;
        this.parser = new JsonParser();

        // If the directory does not exist, create it and add an example file
        if (!configDirectory.exists())
            initConfigDirectory(configDirectory);

        // Throw an exception in case this is not a valid directory
        if (!configDirectory.isDirectory())
            throw new FileNotFoundException("The given directory does not exist or is an invalid directory");
    }

    /**
     * Loads all fluid groups in a given directory.
     */
    public List<FluidGroup> load() {
        // Load all json files and try to create fluid groups for them
        List<FluidGroup> fluidGroups = new ArrayList<FluidGroup>();

        File[] jsonFiles = configDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return "json".equals(FilenameUtils.getExtension(name));
            }
        });

        for (File jsonFile : jsonFiles) {
            FluidGroup fluidGroup = null;
            try {
                fluidGroup = parseFluidGroup(jsonFile);
                fluidGroups.add(fluidGroup);
            } catch (FluidGroupFormatException e) {
                FluidConverters.clog(Level.WARN,
                        "Failed to load file '" + jsonFile.getName() + "': " + e.getMessage());
            }
        }

        return fluidGroups;
    }

    /**
     * Copies a template from the jar to the given directory
     */
    private void copyTemplate(String filename, File directory) throws IOException {
        // Check if the given file exists
        InputStream stream = getClass().getResourceAsStream(Reference.ASSETS_PATH + filename);
        if (stream == null)
            throw new FileNotFoundException("Unable to initialize the config directory: the example file '"
                    + filename + "' does not exist.");

        // Copy the file over
        FileUtils.copyInputStreamToFile(stream, new File(directory, filename));
    }

    /**
     * Initializes the config directory and adds a sample file.
     * @param directory The config directory.
     */
    private void initConfigDirectory(File directory) throws IOException {
        // mkdir -p configDir
        directory.mkdirs();

        copyTemplate("example.json.template", directory);
        copyTemplate("blood.json", directory);
        copyTemplate("custom_recipes.xml", directory);
    }

    /**
     * Parses a FluidGroup from a json file.
     * @param jsonFile The json file which contains data about the fluid group.
     * @return A parsed fluid group.
     */
    private FluidGroup   parseFluidGroup(File jsonFile) throws FluidGroupFormatException {
        JsonElement element = null;
        try {
            element = parser.parse(new FileReader(jsonFile));
        } catch (FileNotFoundException e) {
            // Should not happen (exist check already happened before this call).
        }

        if (!element.isJsonObject()) throw new FluidGroupFormatException("Root element should be an object");

        // Start parsing the root object
        JsonObject fluidGroup = element.getAsJsonObject();

        // Parse required properties
        String groupId = getMandatoryPrimitiveProperty(fluidGroup, "groupId", String.class);

        // Parse fluidElements
        JsonArray fluidElements = getAsJsonArray(fluidGroup.get("fluidElements"));
        if (fluidElements == null)
            throw new FluidGroupFormatException("Mandatory property 'fluidElements' does not exist or is an invalid array");

        List<FluidGroup.FluidElement> fluidElementList = parseFluidElements(fluidElements);

        // Parse optional properties
        String groupName = getOptionalPrimitiveProperty(fluidGroup, "groupName", groupId, String.class);
        float lossRatio = getOptionalPrimitiveProperty(fluidGroup, "lossRatio", 0f, Float.class);
        boolean hasRecipe = getOptionalPrimitiveProperty(fluidGroup, "hasRecipe", true, Boolean.class);
        boolean hasDefaultRecipe = getOptionalPrimitiveProperty(fluidGroup, "hasDefaultRecipe", true, Boolean.class);

        // Create the fluid group object
        FluidGroup result = new FluidGroup(groupId, fluidElementList);
        result.setGroupName(groupName);
        result.setLossRatio(lossRatio);
        result.setHasRecipe(hasRecipe);
        result.setHasDefaultRecipe(hasDefaultRecipe);
        return result;
    }

    private List<FluidGroup.FluidElement> parseFluidElements(JsonArray fluidElements) throws FluidGroupFormatException {
        List<FluidGroup.FluidElement> fluidElementList = new ArrayList<FluidGroup.FluidElement>();

        Iterator<JsonElement> it = fluidElements.iterator();
        while (it.hasNext()) {
            JsonElement fluidElementElement = it.next();
            if (!fluidElementElement.isJsonObject())
                throw new FluidGroupFormatException("Every element inside 'fluidElements' needs to be a valid object");

            JsonObject fluidElementObject = fluidElementElement.getAsJsonObject();
            String fluidName = getMandatoryPrimitiveProperty(fluidElementObject, "fluidName", String.class);
            float value = getMandatoryPrimitiveProperty(fluidElementObject, "value", Float.class);

            try {
                fluidElementList.add(new FluidGroup.FluidElement(fluidName, value));
            } catch (FluidGroup.NoSuchFluidException e) {
                FluidConverters.clog(Level.WARN, e.getMessage());
            }
        }

        if (fluidElementList.size() < 2)
            throw new FluidGroupFormatException("Failed to load at least two fluid elements");

        return fluidElementList;
    }

    private static final Class[] PRIMITIVES = { String.class, Float.class, Boolean.class };
    private static final String[] IS_METHODS = { "isString", "isNumber", "isBoolean" };
    private static final String[] GET_AS_METHODS = { "getAsString", "getAsFloat", "getAsBoolean" };

    /**
     * Parses a mandatory primitive property and returns it value, or throws an exception
     * @param object The json object contain the property
     * @param propertyName The name of the property (i.e. the key in the object)
     * @param clazz The class of the type of the property
     * @param <T> The type of the property
     * @return Returns the value of the mandatory property if it exists, otherwise it throws an exception
     * @throws FluidGroupFormatException Throws an exception in the case of invalid formatting or in case the property
     *                                   is not present in the given object
     */
    private <T> T getMandatoryPrimitiveProperty(JsonObject object, String propertyName, Class<T> clazz) throws FluidGroupFormatException {
        try {
            T property = getOptionalPrimitiveProperty(object, propertyName, null, clazz);
            if (property != null) return property;
        } catch(FluidGroupFormatException e) {
        }

        throw new FluidGroupFormatException("Mandatory property '" + propertyName + "' does not exist or is an invalid " + clazz.getSimpleName());
    }

    /**
     * Parses an optional primitive property and returns it value, or throws an exception
     * @param object The json object contain the property
     * @param propertyName The name of the property (i.e. the key in the object)
     * @param defaultValue The default value for this property
     * @param clazz The class of the type of the property
     * @param <T> The type of the property
     * @return Returns the value of the optional property if it exists, or the default value otherwise
     * @throws FluidGroupFormatException Throws an exception in the case of invalid formatting
     */
    private <T> T getOptionalPrimitiveProperty(JsonObject object, String propertyName, T defaultValue, Class<T> clazz) throws FluidGroupFormatException {
        JsonElement element = object.get(propertyName);
        if (element == null) return defaultValue;

        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();

            for (int i = 0; i < PRIMITIVES.length; ++i) {
                if (PRIMITIVES[i].equals(clazz)) {
                    try {
                        Method isMethod = JsonPrimitive.class.getMethod(IS_METHODS[i]);
                        Method getAsMethod = JsonPrimitive.class.getMethod(GET_AS_METHODS[i]);

                        if ((Boolean)isMethod.invoke(primitive))
                            return (T)getAsMethod.invoke(primitive);
                    } catch (NoSuchMethodException e) {
                        // Should not happen
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        // Should not happen
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // Should not happen
                        e.printStackTrace();
                    }
                }
            }
        }

        throw new FluidGroupFormatException("Optional property '" + propertyName + "' is not a valid " + clazz.getSimpleName());
    }

    private JsonArray getAsJsonArray(JsonElement element) {
        return (element != null && element.isJsonArray()) ? element.getAsJsonArray() : null;
    }

    public static final class FluidGroupFormatException extends Exception {
        public FluidGroupFormatException(String message) {
            super(message);
        }
    }
}
