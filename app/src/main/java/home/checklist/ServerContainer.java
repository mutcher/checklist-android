package home.checklist;

/**
 * Created by Gorbatiuk Illia on 05.04.2016.
 */
public class ServerContainer {
    private static serverConnector _connector = new serverConnector();
    public static serverConnector getConnector() {
        return _connector;
    }
}
