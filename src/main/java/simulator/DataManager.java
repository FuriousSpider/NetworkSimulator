package simulator;

import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import simulator.element.Connection;
import simulator.element.Port;
import simulator.element.device.*;
import util.Values;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataManager {
    //TODO: remember to reset Device.idCounter when deviceList loaded
    public static void open() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                String text = Files.readString(Paths.get(file.toURI()));
                JSONObject object = (JSONObject) new JSONParser().parse(text);
                Data data = new Data();
                ArrayList<Device> deviceResultList = new ArrayList<>();
                ArrayList<Connection> connectionResultList = new ArrayList<>();
                for (Object key : object.keySet()) {
                    if (key instanceof String && key.equals("devices")) {
                         JSONArray deviceArray = (JSONArray) object.get(key);
                         for (Object device : deviceArray) {
                             JSONObject deviceObject = (JSONObject) device;
                             Device.Builder resultDevice = new Device.Builder();
                             for (Object propertyKey : deviceObject.keySet()) {
                                 if (propertyKey instanceof String) {
                                     switch ((String)propertyKey) {
                                         case "id":
                                             resultDevice.id(((Long) deviceObject.get(propertyKey)).intValue());
                                             break;
                                         case "type":
                                             resultDevice.deviceType((String) deviceObject.get(propertyKey));
                                             break;
                                         case "name":
                                             resultDevice.name((String) deviceObject.get(propertyKey));
                                             break;
                                         case "x":
                                             resultDevice.x(((Long) deviceObject.get(propertyKey)).intValue());
                                             break;
                                         case "y":
                                             resultDevice.y(((Long) deviceObject.get(propertyKey)).intValue());
                                             break;
                                         case "macAddress":
                                             resultDevice.macAddress((String) deviceObject.get(propertyKey));
                                             break;
                                         case "portList":
                                             JSONArray portArray = (JSONArray) deviceObject.get(propertyKey);
                                             ArrayList<Port> portList = new ArrayList<>();
                                             for (Object port : portArray) {
                                                 JSONObject portObject = (JSONObject) port;
                                                 Port resultPort = new Port();
                                                 for (Object portKey : portObject.keySet()) {
                                                     if (portKey instanceof String) {
                                                         switch ((String) portKey) {
                                                             case "id":
                                                                 resultPort.setId(((Long) portObject.get(portKey)).intValue());
                                                                 break;
                                                             case "anInterface":
                                                                 resultPort.setNewInterface();
                                                                 resultPort.setIpAddress((String) portObject.get(portKey));
                                                                 break;
                                                             case "isPortTaken":
                                                                 boolean isPortTaken = (Boolean) portObject.get(portKey);
                                                                 if (isPortTaken) {
                                                                     resultPort.reservePort();
                                                                 } else {
                                                                     resultPort.releasePort();
                                                                 }
                                                                 break;
                                                         }
                                                     }
                                                 }
                                                 portList.add(resultPort);
                                             }
                                             resultDevice.portList(portList);
                                             break;
                                         case "anInterface":
                                             resultDevice.ipAddress((String) deviceObject.get(propertyKey));
                                             break;
                                         case "associationTable":
                                             JSONArray entryArray = ((JSONArray) deviceObject.get(propertyKey));
                                             ArrayList<Pair<String, Integer>> associationTableResult = new ArrayList<>();
                                             for (Object entry : entryArray) {
                                                 JSONObject entryObject = (JSONObject) entry;
                                                 String first = (String) entryObject.get("key");
                                                 Integer second = ((Long) entryObject.get("value")).intValue();
                                                 Pair<String, Integer> pair = new Pair<>(first, second);
                                                 associationTableResult.add(pair);
                                             }
                                             resultDevice.associationTable(associationTableResult);
                                             break;
                                         case "routingTable":
                                             JSONArray routingTableArray = ((JSONArray) deviceObject.get(propertyKey));
                                             Map<String, String> routingTable = new HashMap<>();
                                             for (Object entry : routingTableArray) {
                                                 JSONObject entryObject = (JSONObject) entry;
                                                 String first = (String) entryObject.get("key");
                                                 String second = (String) entryObject.get("value");
                                                 routingTable.put(first, second);
                                             }
                                             resultDevice.routingTable(routingTable);
                                             break;

                                     }
                                 }
                             }
                             deviceResultList.add(resultDevice.build());
                         }
                        data.setDeviceArrayList(deviceResultList);
                    } else if (key instanceof String && key.equals("connections")) {
                        JSONArray connectionsArray = (JSONArray) object.get(key);
                        for (Object connection : connectionsArray) {
                            JSONObject connectionObject = (JSONObject) connection;
                            Connection.Builder connectionBuilder = new Connection.Builder();
                            Double colorRed = 0.0;
                            Double colorGreen = 0.0;
                            Double colorBlue = 0.0;
                            for (Object propertyKey : connectionObject.keySet()) {
                                if (propertyKey instanceof String) {
                                    switch ((String) propertyKey) {
                                        case "id":
                                            connectionBuilder.id(((Long)connectionObject.get(propertyKey)).intValue());
                                            break;
                                        case "portPair":
                                            JSONObject portPairObject = (JSONObject) connectionObject.get(propertyKey);
                                            Integer first = ((Long) portPairObject.get("first")).intValue();
                                            Integer second = ((Long) portPairObject.get("second")).intValue();
                                            connectionBuilder.portPair(new Pair<>(first, second));
                                            break;
                                        case "colorRed":
                                            colorRed = (Double) connectionObject.get(propertyKey);
                                            break;
                                        case "colorGreen":
                                            colorGreen = (Double) connectionObject.get(propertyKey);
                                            break;
                                        case "colorBlue":
                                            colorBlue = (Double) connectionObject.get(propertyKey);
                                            break;
                                    }
                                }
                            }
                            connectionBuilder.color(Color.color(colorRed, colorGreen, colorBlue));
                            connectionResultList.add(connectionBuilder.build());
                        }
                        data.setConnectionArrayList(connectionResultList);
                    } else if (key instanceof String && key.equals("options")) {
                        JSONArray optionsArray = (JSONArray) object.get(key);
                        for (Object option : optionsArray) {
                            JSONObject optionObject = (JSONObject) option;
                            for (Object optionKey : optionObject.keySet()) {
                                if (optionKey instanceof String) {
                                    switch ((String)optionKey) {
                                        case "deviceIdCounter":
                                            Device.setIdCounter(((Long)optionObject.get(optionKey)).intValue());
                                            break;
                                        case "portIdCounter":
                                            Port.setIdCounter(((Long)optionObject.get(optionKey)).intValue());
                                            break;
                                        case "connectionIdCounter":
                                            Connection.setIdCounter(((Long)optionObject.get(optionKey)).intValue());
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
                Engine.getInstance().loadData(data);
            } catch (Exception e) {
                Engine.getInstance().logError(Values.ERROR_OPEN_FILE_ERROR);
            }
        }
    }

    public static void save(Data data) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                JSONObject dataObject = new JSONObject();
                JSONArray devicesArray = new JSONArray();
                for (Device device : data.getDeviceArrayList()) {
                    JSONObject deviceObject = new JSONObject();
                    deviceObject.put("id", device.getId());
                    deviceObject.put("type", device.getDeviceType());
                    deviceObject.put("name", device.getName());
                    deviceObject.put("x", device.getX());
                    deviceObject.put("y", device.getY());
                    deviceObject.put("macAddress", device.getMacAddress());
                    JSONArray portArray = new JSONArray();
                    for (Port port : device.getPortList()) {
                        JSONObject portObject = new JSONObject();
                        portObject.put("id", port.getId());
                        if (port.hasInterface()) {
                            portObject.put("anInterface", port.getIpAddress());
                        }
                        portObject.put("isPortTaken", port.isPortTaken());
                        portArray.add(portObject);
                    }
                    deviceObject.put("portList", portArray);
                    if (device instanceof EndDevice) {
                        deviceObject.put("anInterface", ((EndDevice) device).getIpAddress());
                    } else if (device instanceof Switch) {
                        JSONArray associationTableArray = new JSONArray();
                        for (Pair<String, Integer> pair : ((Switch) device).getAssociationTable()) {
                            JSONObject associationTableObject = new JSONObject();
                            associationTableObject.put("key", pair.getKey());
                            associationTableObject.put("value", pair.getValue());
                            associationTableArray.add(associationTableObject);
                        }
                        deviceObject.put("associationTable", associationTableArray);
                    } else if (device instanceof Router) {
                        JSONArray routingTableArray = new JSONArray();
                        for (String key : ((Router) device).getRoutingTableCopy().keySet()) {
                            JSONObject routingTableObject = new JSONObject();
                            routingTableObject.put("key", key);
                            routingTableObject.put("value", ((Router) device).getRoutingTableCopy().get(key));
                            routingTableArray.add(routingTableObject);
                        }
                        deviceObject.put("routingTable", routingTableArray);
                    }

                    devicesArray.add(deviceObject);
                }
                dataObject.put("devices", devicesArray);

                JSONArray connectionsArray = new JSONArray();
                for (Connection connection : data.getConnectionArrayList()) {
                    JSONObject connectionObject = new JSONObject();
                    connectionObject.put("id", connection.getId());
                    JSONObject portPairObject = new JSONObject();
                    portPairObject.put("first", connection.getPortPair().getKey());
                    portPairObject.put("second", connection.getPortPair().getValue());
                    connectionObject.put("portPair", portPairObject);
                    connectionObject.put("colorRed",  connection.getColor().getRed());
                    connectionObject.put("colorGreen",  connection.getColor().getGreen());
                    connectionObject.put("colorBlue",  connection.getColor().getBlue());
                    connectionsArray.add(connectionObject);
                }
                dataObject.put("connections", connectionsArray);

                JSONArray optionsArray = new JSONArray();
                JSONObject optionObject = new JSONObject();
                optionObject.put("key", "deviceIdCounter");
                optionObject.put("value", Device.getIdCounter());
                optionsArray.add(optionObject);

                optionObject = new JSONObject();
                optionObject.put("key", "portIdCounter");
                optionObject.put("value", Port.getIdCounter());
                optionsArray.add(optionObject);


                optionObject = new JSONObject();
                optionObject.put("key", "connectionIdCounter");
                optionObject.put("value", Connection.getIdCounter());
                optionsArray.add(optionObject);

                dataObject.put("options", optionsArray);
                //TODO: save macAddressTable from utils
                Files.writeString(Paths.get(file.toURI()), dataObject.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Data {
        private ArrayList<Device> deviceArrayList = new ArrayList<>();
        private ArrayList<Connection> connectionArrayList = new ArrayList<>();

        public void setDeviceArrayList(ArrayList<Device> deviceArrayList) {
            this.deviceArrayList = deviceArrayList;
        }

        public void setConnectionArrayList(ArrayList<Connection> connectionArrayList) {
            this.connectionArrayList = connectionArrayList;
        }

        public ArrayList<Device> getDeviceArrayList() {
            return deviceArrayList;
        }

        public ArrayList<Connection> getConnectionArrayList() {
            return connectionArrayList;
        }
    }
}