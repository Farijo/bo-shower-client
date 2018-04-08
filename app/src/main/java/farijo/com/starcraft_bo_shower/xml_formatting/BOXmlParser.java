package farijo.com.starcraft_bo_shower.xml_formatting;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import farijo.com.starcraft_bo_shower.player.SC2Action;

import static farijo.com.starcraft_bo_shower.player.SC2Action.NO_TIMING;

public class BOXmlParser {

    private XmlPullParser parser;
    private boolean allTimingsAvailable = true;
    private List<SC2Action> actions = new ArrayList<>();

    public BOXmlParser() throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        parser = factory.newPullParser();
    }

    public void parseBO(File file) throws IOException, XmlPullParserException {
        parser.setInput(new FileInputStream(file), "UTF-8");

        final SC2Action TMPL_START_ACT = new SC2Action();
        SC2Action currentAction = TMPL_START_ACT;
        String name = null;
        int previousTiming = 0;

        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.END_TAG:
                    name = null;
                    break;
                case XmlPullParser.START_TAG:
                    switch (parser.getName()) {
                        case Tags.Name.ACTION:
                            if(currentAction != TMPL_START_ACT) {
                                actions.add(currentAction);
                            }
                            currentAction = new SC2Action();
                            break;
                        case Tags.Name.ONFINISH:
                            currentAction.onFinish = intAttributeValue(Tags.Attribute.REF, -1);
                            break;
                        case Tags.Name.PROD:
                            currentAction.resourceIcon = SC2Action.getDrawableId(attributeValue(Tags.Attribute.IMAGE));
                            currentAction.count = intAttributeValue(Tags.Attribute.COUNT, 1);
                        case Tags.Name.POP:
                        case Tags.Name.TIME:
                        case Tags.Name.DETAILS:
                            name = parser.getName();
                            break;
                    }
                    break;

                case XmlPullParser.TEXT:
                    final String value = parser.getText().trim();
                    if (name == null || value.isEmpty()) {
                        break;
                    }
                    switch (name) {
                        case Tags.Name.POP:
                            currentAction.population = value;
                            break;
                        case Tags.Name.TIME:
                            currentAction.strTiming = value;
                            currentAction.timing = strToIntTiming(value, NO_TIMING);
                            if(allTimingsAvailable) {
                                if(currentAction.timing == NO_TIMING) {
                                    allTimingsAvailable = false;
                                } else {
                                    currentAction.deltaTiming = 100L * (currentAction.timing - previousTiming);
                                    previousTiming = currentAction.timing;
                                }
                            }
                            break;
                        case Tags.Name.PROD:
                            currentAction.name = value;
                            break;
                        case Tags.Name.DETAILS:
                            currentAction.details = value;
                            break;
                    }
                    break;
            }
            event = parser.next();
        }
    }

    public boolean areAllTimingsAvailable() {
        return allTimingsAvailable;
    }

    public List<SC2Action> getActions() {
        return new ArrayList<>(actions);
    }

    private String attributeValue(String attributeName) {
        return parser.getAttributeValue(null, attributeName);
    }

    private int intAttributeValue(String attributeName, int defaultValue) {
        try {
            return Integer.parseInt(parser.getAttributeValue(null, attributeName));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static int strToIntTiming(String str, int defaultValue) {
        final int sepIndex = str.indexOf(':');
        final String minutes = str.substring(0, sepIndex);
        final String seconds = str.substring(sepIndex + 1);
        try {
            return Integer.parseInt(minutes) * 60 + Integer.parseInt(seconds);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
