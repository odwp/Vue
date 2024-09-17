<h1/>Emporia Vue local integration with Hubitat.</h1>

Beta V 0.5 published 26 Aug 2024


This is a solution to locally integrate Emporia Vue V2 and V3 power monitoring into Hubitat.  It is currently an early Alpha version of the files.  The existing files only support Vue2 devices right now.  V3 support will come later.

If you have wandered here on your own, be aware integrating Emporia Vue devices locally to Hubitat is an extremely non-trivial process.  This requires reprogramming the Vues you want to integrate.  <b/>OPENING AND REPROGRAMMING YOUR VUE COULD VOID YOUR EMPORIA WARRANTY!!!</b>

<b/>The use of these files and process is at your own risk.  There is no warranty either expressed or implied.</b>
____________________________________________________________

To perform this process you will need the following items:

1. <b>Python</b> installed python on your computer.  Use the latest version that is in bugfix Maintenance status, 3.12.5 as I write this.  <a href="https://www.python.org/downloads/"> Python Downloads </a><br>

2. <b>ESPHome</b> installed on the same computer. -> <a href="https://esphome.io/guides/installing_esphome.html">Installing ESPHome Manually<a/> <br>

3. <b>USB Serial adapter</b> to connect your PC and your Vue-> <a href="https://www.amazon.com/gp/product/B09KXT6W46/ref=ppx_yo_dt_b_asin_title_o00_s01?ie=UTF8&psc=1"> DSD TECH SH-U09B3 USB Type C to TTL Serial Adapter with CP2102N Chip</a><br>
(There are others you can get, but if you intend to use a V3 Vue you will need a Serial adapter that outputs 3.3V.)<br>

4. <b>Female-Female dupont jumper wires.</b> You will use these to connect the USB serial adapter to your Vue.  <a href="https://www.amazon.com/California-JOS-Breadboard-Optional-Multicolored/dp/B0BRTJQGS6/ref=sr_1_6?crid=2ROTJIVG8ZYTA&dib=eyJ2IjoiMSJ9.1IBupQO5VvdNl0r0O-7gG4vy1PpPZjMk0T7UCMO_WARSjFudrFpmolQe-vHYusNtg8G1NNFtfc4ttUTkzVjOBsVP89Ch7n226AngUMiDyEvYtEhU2sEbMUaR19vhCD3oThpP2dJd0KJUrWecIC0uLuR_IgFwWc8K-XK2kf-Z7NUvoSwXDDzfpd24eTVIGF768Gi8l2STnTu88kl_t_OJ_1Vhu1hjOEyhbmCKEXN6aRZvoRGGQNg6iogImNJFLC82WbDtUCVKrgkZV1eYrt-wjcrYK_bKSCd_8YgHuj8MR2I.fNp-LDt8zfoHsVZ1i6GBVNj5p04qHJNwpMnkVHU3Di4&dib_tag=se&keywords=female-female%2Bdupont%2Bwires&qid=1723604929&sprefix=female-female%2Bdupo%2Caps%2C106&sr=8-6&th=1"> 40 PCS 20 CM (8 inch) Breadboard Jumper Wires Wire Length Optional Dupont Cable Assorted Kit Female to Female Multicolored Ribbon Cables</a>

5. <b>Jonathan Bradshaw's ESPHome-API-Library</b> saved in the <i>Library Code</i> page of the Hubitat hub you will use. Create a new file in Library Code. <i>Import</i> the code using following link and save it ->  https://raw.githubusercontent.com/bradsjm/hubitat-drivers/main/ESPHome/ESPHome-API-Library.groovy
_____________________________________________________________          
Here is a detailed description of how to reprogram your Vue V2.  <a href="https://github.com/emporia-vue-local/esphome"> Setting up Emporia Vue 2 with ESPHome</a>  

Here are the jumper connection to make.  They are a little different than digiblurDIY shows, however he does list these as options if your adapter has the connections available.<br><br>
  USB Serial Adapter  <-->  Vue<br>
         TXD <--> TXD<br>
         RXD <--> RXD<br>
         GND <--> GND<br>
         DTR <--> IO0<br>
         DSR <--> EN<br>
         3V3 <--> VCC 3V3<br>
         
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


 
