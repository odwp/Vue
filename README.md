<h1/>Emporia Vue local integration with Hubitat.</h1>

This is the nucleus of a solution to locally integrate Emporia Vue V2 and V3 power monitoring into Hubitat.  It is currently an early Alpha version of the files.  The existing files only support Vue2 devices right now.  V3 support will come later.

If you have wandered here on your own, be aware integrating Emporia Vue devices locally to Hubitat is an extremely non-trivial process.  This requires reprogramming the Vues you want to integrate.  <b/>OPENING AND REPROGRAMMING YOUR VUE WILL VOID YOUR EMPORIA WARRANTY!!!</b>

<b/>The use of these files and process is at your own risk.  There is no warranty either explicit or implied.</b>
____________________________________________________________

To perform this process you will need the following items:

Install ESPHome on the PC you will use to reprogram youe Vue -> https://esphome.io/ <br>

USB Serial adapter to connect your PC and your Vue-> https://www.amazon.com/gp/product/B09KXT6W46/ref=ppx_yo_dt_b_asin_title_o00_s01?ie=UTF8&psc=1<br>
(There are others you can get, but if you intend to use a V3 Vue you will need a Serial adapter that outputs 3.3V.)<br>

Install Jonathan Bradshaw's ESPHome-API-Library on the Hubitat hub you will use.  ->  https://raw.githubusercontent.com/bradsjm/hubitat-drivers/main/ESPHome/ESPHome-API-Library.groovy
_____________________________________________________________          
Here is a detailed description of how to reprogram your Vue V2.   [https://github.com/emporia-vue-local/esphome](https://github.com/emporia-vue-local/esphome#backing-up--flashing-the-vue-2)  
digiblurDIY created his process to support Home Assistant and MQTT. Ignore those references for this Hubitat implementation.  There are changes I made to the yaml file that may/will break its ability to support HA and MQTT.  It is not my intent to support those.

The driver files have been coded expecting the yaml to be fundamentally unchanged with the exception of the "substitutions" section.  Naming of the individual circuits MUST be performed in the Hubitat devices that are created by this process.  Changing the circuit names in the yaml file will break the solution.

Panelboards typically have more circuits that one Vue can monitor, so I devleoped this with the panelboard as the device that is directly installed by the user.  Within the Panelboard the individual Vue devices and thier branch circuits can be created.  Installing the Vue directly may or may not work.  I haven't tested this use, and I do not intend to suport that.

This is a portion of the substitutions section of the yaml file that will need to be checked and updated.

![image](https://github.com/user-attachments/assets/be6748c6-937b-43c4-8c96-ea69314212a5)

The installation of Vues for a panelboard begins with the installation of the panelboard device.

![image](https://github.com/user-attachments/assets/a2b0453b-bfef-4dfd-a4e3-9e011e8ce389)

The next step is adding the Vues to the panelboard.  This will automatically add sixteen branch circuit devices as child devices of the Vue.

![image](https://github.com/user-attachments/assets/40e362a4-84c5-4c11-a2a7-0c9552f0930d)

This is how the various devices will look when installed.

![image](https://github.com/user-attachments/assets/956f25ce-7a8e-4f71-8b29-bed41e87c9dd)


 
