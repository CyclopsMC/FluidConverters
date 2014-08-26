package org.cyclops.fluidconverters.config

import java.io.{FileReader, BufferedReader, File}
import java.util.regex.Pattern

import com.google.gson.{JsonIOException, JsonSyntaxException, Gson}
import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.Level
import org.cyclops.fluidconverters.{LoggerHelper, Reference}

import scala.collection.mutable.ListBuffer

/**
 * Load the configs.
 * @author rubensworks
 */
object ConfigLoader {

    final val BLOOD_TEMPLATE = "blood.json"
    final val AQUALAVA_TEMPLATE = "_aqualava.json"
    final val RESOURCE_TEMPLATE_PATH_BLOOD = "/assets/" + Reference.MOD_ID + "/" + BLOOD_TEMPLATE
    final val RESOURCE_TEMPLATE_PATH_AQUALAVA = "/assets/" + Reference.MOD_ID + "/" + AQUALAVA_TEMPLATE
    final val CONFIG_PATTERN = Pattern.compile("^[^_].*\\.json")

    private def createTemplate(configDirectory: String, name: String, path: String) {
        val template = new File(configDirectory, name)
        if (!template.exists()) {
            val is = getClass.getResourceAsStream(path)
            FileUtils.copyInputStreamToFile(is, template)
        }
    }

    /**
     * Initialize the config directory.
     * It will create a template config if one does not exist yet.
     * @param configDirectory The name of the directory to initialize.
     * @return The created directory file reference.
     */
    def init(configDirectory: String): File = {
        // Check if the directory exists, and create one if negative.
        val rootFolder = new File(configDirectory)
        if (!rootFolder.exists()) {
            rootFolder.mkdir()
        }

        // Put the template files in the config dir if it is not in there yet.
        createTemplate(configDirectory, BLOOD_TEMPLATE, RESOURCE_TEMPLATE_PATH_BLOOD)
        createTemplate(configDirectory, AQUALAVA_TEMPLATE, RESOURCE_TEMPLATE_PATH_AQUALAVA)

        rootFolder
    }

    /**
     * Find all the fluid group configs in the given root folder.
     * It will search the child folders recursively.
     * @param rootFolder The root folder.
     * @return The found configs.
     */
    def findFluidGroups(rootFolder: File): ListBuffer[FluidGroup] = {
        findFluidGroups(rootFolder, rootFolder)
    }

    private def findFluidGroups(rootFolder: File, currentFolder : File): ListBuffer[FluidGroup] = {
        val configs = ListBuffer[FluidGroup]()
        for(file <- currentFolder.listFiles) {
            if(file.isFile && CONFIG_PATTERN.matcher(file.getName).matches()) {
                try {
                    configs += loadFluidGroup(file)
                    LoggerHelper.log("Loaded config %s.".format(file.getName));
                } catch {
                    case e : JsonSyntaxException => {
                        LoggerHelper.log(Level.ERROR, "The config %s has an invalid syntax.".format(file.getName))
                        System.err.print(e)
                    }
                    case e : JsonIOException => {
                        LoggerHelper.log(Level.ERROR, "Something went wrong while reading %s.".format(file.getName))
                        System.err.print(e)
                    }
                }
            } else if(file.isDirectory) {
                configs ++= findFluidGroups(file)
            } else {
                LoggerHelper.log("Skipped config %s.".format(file.getName))
            }
        }
        configs
    }

    def loadFluidGroup(file : File) : FluidGroup = {
        val gson = new Gson
        gson.fromJson(new BufferedReader(new FileReader(file)), classOf[FluidGroup])
    }

}
