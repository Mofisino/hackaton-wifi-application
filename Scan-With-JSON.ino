#include <ESP8266WebServer.h>
#include "ESP8266WiFi.h"
#include <WiFiClient.h> 
#include <cJSON.h> //newly added, to be tested

//web server
#define webServerPort 80
ESP8266WebServer server(webServerPort);

short const TXPOWER = -34;
double const PATH_LOSS_COMPONENT = 2.5;
const char * AP_SSID = "ESP8266-AP";
const char * AP_PASSWORD = "rootroot";

struct wifi_network
{
  char * ssid;
  char * password;
  short distance;
  //might as well add the encryption type here (use enum maybe?)
  
  wifi_network(char * _ssid, char * _password, short _distance) : distance(_distance)
  {
    ssid = (char*)malloc((strlen(_ssid) + 1) * sizeof(char));
    strcpy(ssid, _ssid);
    password = (char*)malloc((strlen(_password) + 1) * sizeof(char));
    strcpy(password, _password);
  }
};

double calculateDistance(short RSSI)
{
  //Formula:
  //d = 10 ^ ((TxPower – RSSI) / (10 * n))
  //TxPower is the RSSI measured at 1m from a known AP. In our case it's -34 (-ish)
  //n is the propagation constant or path-loss exponent. For example: 2.7 to 4.3 (Free space has n = 2 for reference).
  //RSSI is the measured RSSI
  //d is the distance in meters
  //return (double)(10.0 ^ ((TXPOWER - RSSI) / (10 * PATH_LOSS_COMPONENT)));
  return pow(10.0,(TXPOWER - RSSI) / (10 * PATH_LOSS_COMPONENT));
}

char * createJSONArray(int numberOfNetworks)
{
  char *jsonArray;
  cJSON *root, *networks, *network;

  // create root node and array 
  root = cJSON_CreateObject();
  networks = cJSON_CreateArray();

  // add networks array to root 
  cJSON_AddItemToObject(root, "networks", networks);

  for (int i = 0; i < numberOfNetworks; ++i)
  {
    cJSON_AddItemToArray(networks, network = cJSON_CreateObject());
    cJSON_AddItemToObject(network, "SSID", cJSON_CreateString(WiFi.SSID(i).c_str())); //see if this needs any parsing/casting
    //cJSON_AddItemToObject(network, "password", cJSON_CreateString("wifi_password")); //we'll see where we get this one from
    cJSON_AddItemToObject(network, "distance", cJSON_CreateNumber(calculateDistance(WiFi.RSSI(i))));
  }

  //DEBUGGING: print everything
  jsonArray = cJSON_Print(root);
  printf("%s\n", jsonArray);
  //end-of-DEBUGGING

  // free all objects under root and root itself
  cJSON_Delete(root);

  return jsonArray;
}

void setup() {
  Serial.begin(115200);

  // Set WiFi to station mode and disconnect from an AP if it was previously connected
  WiFi.mode(WIFI_STA);
  WiFi.disconnect();
  delay(100);

  //Setup access point
  Serial.println();
  Serial.print("Configuring access point...");
 
  WiFi.softAP(AP_SSID, AP_PASSWORD);

  IPAddress myIP = WiFi.softAPIP();
  
  Serial.print("AccessPoint IP address: ");
  Serial.println(myIP);

  //Setup server
  server.begin();
  server.on("/", handleRoot);
  server.on("/scan", handleScan);

  Serial.println("Setup done");
}

void loop()
{
  //use milis() instead of delay

  server.handleClient();

  // Wait a bit before scanning again
  //delay(5000);//delay(300000); //5 minutes for instance
}

void handleRoot() 
{
  server.send(200, "text/plain", "Hello from ESP8266!\n\n Go to /scan to get network info.");
}

void handleScan()
{
  Serial.println("Scan started");

  // WiFi.scanNetworks will return the number of networks found
  int n = WiFi.scanNetworks();
  Serial.println("Scan done");

  if (n == 0)
  {
    Serial.println("No networks found");
  } 
  else 
  {
    Serial.print(n);
    Serial.println(" networks found");
  }
  
  Serial.println('\n');

  server.send(200, "application/json", createJSONArray(n));
}
