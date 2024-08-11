<h1/>Emporia Vue local integration with Hubitat.</h1>

This is the nucleus of a solution to locally integrate Emporia Vue V2 and V3 power monitoring into Hubitat.  It is currently an early Alpha version of the files.  The existing files only support Vue2 devices right now.  V3 support will come later.

If you have wandered here on your own, be aware integrating Emporia Vue devices locally to Hubitat is an extremely non-trivial process.  This requires reprogramming the Vues you want to integrate.  <b/>OPENING AND REPROGRAMMING YOUR VUE WILL VOID YOUR EMPORIA WARRANTY!!!</b>

<b/>The use of these files and process is at your own risk.  There is no warranty either explicit or implied.</b>

To perform this process you will need the following items:

Install ESPHome -> https://esphome.io/ <br>
USB Serial adapter -> https://www.amazon.com/gp/product/B09KXT6W46/ref=ppx_yo_dt_b_asin_title_o00_s01?ie=UTF8&psc=1<br>
(There are others you can get, but if you intend to use a V3 Vue you will need a Serial adapter that outputs 3.3V.)<br>
          
Here is a detailed description of how to reprogram your Vue V2.   [https://github.com/emporia-vue-local/esphome](https://github.com/emporia-vue-local/esphome#backing-up--flashing-the-vue-2)  
digiblurDIY created his process to support Home Assistant and MQTT. Ignore those references for this Hubitat implementation.  There are changes I made to the yaml file that may/will break its ability to support HA and MQTT.  It is not my intent to support those.

The driver files have been coded expecting the yaml to be fundamentally unchanged with the exception of the "substitutions" section.  Naming of the individual circuits MUST be performed in the Hubitat devices that are created by this process.  Changing the circuit names in the yaml file will break the solution.

 
