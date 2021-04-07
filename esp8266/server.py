# Checkpoint 3

import machine
import ssd1306
import time
import network
import socket
import urequests
import json

# LCD Setup
i2c = machine.I2C(-1, machine.Pin(5), machine.Pin(4)) # setup I2C for screen
oled = ssd1306.SSD1306_I2C(128, 32, i2c)        # setup screen as I2C device
led = machine.Pin(2, machine.Pin.OUT)

# Connect ESP8266 to WiFi
# Example from: http://docs.micropython.org/en/latest/esp8266/tutorial/network_basics.html
sta_if = network.WLAN(network.STA_IF)         # setup ESP8266 as a station
ap_if = network.WLAN(network.AP_IF)           # setup ESP8266 as AP

def do_connect():
    if not sta_if.isconnected():
        print('connecting to network...')
        sta_if.active(True)
        sta_if.connect('Fam', 'pinkcoconut')  #(<SSID>, <password>)
        while not sta_if.isconnected():
            pass
    print('network config:', sta_if.ifconfig())

do_connect()


#Initiate on-board (server) socket
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# s.setblocking(False)
s.bind(('', 80)) #empty string is localhost
s.listen(5)

#Listen for incoming commands from the phone (client)
while True:

  #Setup time
  oled.fill(0)
  timeMeasure = time.time()
  seconds = (timeMeasure) % 60
  minutes = int((timeMeasure / 60) % 60)
  hours = int((timeMeasure / 3600) % 24)
  output = "Time: " + str(hours) + ":" + str(minutes) + "." + str(seconds)
  oled.text(output, 0, 0)

  #Accept incoming Connection
  conn, addr = s.accept()
  print('Got a connection from %s' % str(addr))

  #parse request
  request = conn.recv(1024)
  request = str(request)
  print('Content = %s' % request)

  #Extract Command
  sub_str = " "
  occurrence = 2
  val = -1             # Finding nth occurrence of substring
  for i in range(0, occurrence):
    val = request.find(sub_str, val + 1)
  command = request[16:val]
  command = command.replace("%20", " ")

  #Process Command
  if (command == "on"):
    oled.show()
  elif (command == "off"):
    oled.fill(0)
    oled.show()
  else:
    #Show Command
    oled.text("Cmnd: " + command, 0, 10)
    oled.show()
  

  #TOGGLE LIGHTS
  led_on = request.find('/?led=on')
  led_off = request.find('/?led=off')
  if led_on == 6:
    print('LED ON')
    led.value(1)
  if led_off == 6:
    print('LED OFF')
    led.value(0)


  #GENERATE RESPONSE
  response = "All Good"
  conn.send('HTTP/1.1 200 OK\n')
  conn.send('Content-Type: text/html\n')
  conn.send('Connection: close\n\n')
  conn.sendall(response)
  conn.close()


  oled.show()
  time.sleep(0.5)












# Execute: exec(open("server.py").read())

