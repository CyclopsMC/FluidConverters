package org.cyclops.fluidconverters.config

import java.io.{FileReader, BufferedReader, InputStream, File}
import java.util.regex.Pattern

import com.google.gson.{JsonIOException, JsonSyntaxException, Gson}
import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.Level
import org.cyclops.fluidconverters.{LoggerHelper, Reference}

import scala.collection.mutable

/**
 * @author rubensworks
 */
object ConfigLoader {

    final val BLOOD_TEMPLATE = "blood.json"
    final val RESOURCE_TEMPLATE_PATH = "/assets/" + Reference.MOD_ID + "/" + BLOOD_TEMPLATE
    final val CONFIG_PATTERN = Pattern.compile("^[^_].*\\.json")

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

        // Put the blood template file in the config dir if it is not in there yet.
        val template = new File(configDirectory, BLOOD_TEMPLATE)
        if (!template.exists()) {
            val is = getClass.getResourceAsStream(RESOURCE_TEMPLATE_PATH)
            FileUtils.copyInputStreamToFile(is, template)
        }

        rootFolder
    }

    /**
     * Find all the fluid group configs in the given root folder.
     * It will search the child folders recursively.
     * @param rootFolder The root folder.
     * @return The found configs.
     */
    def findFluidGroups(rootFolder: File): Seq[FluidGroup] = {
        findFluidGroups(rootFolder, rootFolder)
    }

    private def findFluidGroups(rootFolder: File, currentFolder : File): Seq[FluidGroup] = {
        val configs = mutable.LinearSeq[FluidGroup]()
        for(file <- currentFolder.listFiles) {
            if(file.isFile && CONFIG_PATTERN.matcher(file.getName).matches()) {
                try {
                    val config = loadFluidGroup(file)
                    configs :+ config
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
                configs :+ findFluidGroups(file)
            } else {
                LoggerHelper.log("Skipped config %s.".format(file.getName))
            }
        }
        configs
    }

    def loadFluidGroup(file : File) {
        val gson = new Gson
        gson.fromJson(new BufferedReader(new FileReader(file)), classOf[FluidGroup])
    }

}
