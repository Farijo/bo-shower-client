package farijo.com.starcraft_bo_shower.xml_formatting;

import android.content.Context;
import android.content.res.Resources;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import farijo.com.starcraft_bo_shower.player.SC2Action;

import static farijo.com.starcraft_bo_shower.player.SC2Action.NO_TIMING;

public class BOXmlWriter {

    private XmlSerializer xmlSerializer;
    private List<SC2Action> actions = new ArrayList<>();

    public void writeActions(Context context, String boName) throws IOException {

        StringWriter writer = startBoFile();

        serializeActions(context);

        endBoFile();

        writeXmlToFile(context, boName, writer.toString());
    }

    private StringWriter startBoFile() throws IOException {
        xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        xmlSerializer.setOutput(writer);
        xmlSerializer.startDocument("UTF-8", true);
        xmlSerializer.startTag(null, Tags.Name.ACTIONS);
        return writer;
    }

    private void serializeActions(Context context) throws IOException, Resources.NotFoundException {
        for (SC2Action action : actions) {
            xmlSerializer.startTag(null,Tags.Name.ACTION);

            serializeTiming(action);
            serializePopulation(action);
            serializeOnFinish(action);
            serializeProd(action, context);
            serializeDetails(action);

            xmlSerializer.endTag(null, Tags.Name.ACTION);
        }
    }

    private void serializePopulation(SC2Action action) throws IOException {
        if(action.population != null) {
            xmlSerializer.startTag(null, Tags.Name.POP);
            xmlSerializer.text(action.population);
            xmlSerializer.endTag(null, Tags.Name.POP);
        }
    }

    private void serializeTiming(SC2Action action) throws IOException {
        if(action.timing > NO_TIMING) {
            xmlSerializer.startTag(null, Tags.Name.TIME);
            xmlSerializer.text(action.timing / 60 + ":" + action.timing % 60);
            xmlSerializer.endTag(null, Tags.Name.TIME);
        }
    }

    private void serializeOnFinish(SC2Action action) throws IOException {
        if(action.isDepedentOfEntity()) {
            xmlSerializer.startTag(null, Tags.Name.ONFINISH);
            xmlSerializer.attribute(null, Tags.Attribute.REF, String.valueOf(action.onFinish));
            xmlSerializer.endTag(null, Tags.Name.ONFINISH);
        }
    }

    private void serializeProd(SC2Action action, Context context) throws IOException, Resources.NotFoundException {
        final String resourceName = context.getResources().getResourceName(action.resourceIcon);
        final int firstLetterIndex = resourceName.lastIndexOf("_") + 1;

        xmlSerializer.startTag(null, Tags.Name.PROD);
        xmlSerializer.attribute(null, Tags.Attribute.IMAGE, resourceName);
        xmlSerializer.text(Character.toUpperCase(resourceName.charAt(firstLetterIndex)) + resourceName.substring(firstLetterIndex + 1));
        xmlSerializer.endTag(null, Tags.Name.PROD);
    }

    private void serializeDetails(SC2Action action) throws IOException {
        if(action.details != null) {
            xmlSerializer.startTag(null, Tags.Name.DETAILS);
            xmlSerializer.text(action.details);
            xmlSerializer.endTag(null, Tags.Name.DETAILS);
        }
    }

    private void endBoFile() throws IOException {
        xmlSerializer.endTag(null, Tags.Name.ACTIONS);
        xmlSerializer.endDocument();
        xmlSerializer.flush();
    }

    private void writeXmlToFile(Context context, String boName, String xml) throws IOException {
        final File file = createFileAndOverFolders(new File(context.getFilesDir(), "files"), boName + ".sc2bo.xml");
        final FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(xml.getBytes());
        outputStream.close();
    }

    private static File createFileAndOverFolders(File localRoot, String fileName) throws IOException {
        final File file = new File(localRoot, fileName);
        file.getParentFile().mkdirs();
        file.createNewFile();
        return file;
    }
}
