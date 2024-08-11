/**
 *  MIT License
 *  Copyright 2022 Jonathan Bradshaw (jb@nrgup.net)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
metadata {
    definition(
        name: 'ESPHome Emporia Panelboard',
        namespace: 'esphome',
        author: 'Wayne Pirtle',
        singleThreaded: true,
   //     importUrl: 'https://raw.githubusercontent.com/bradsjm/hubitat-drivers/main/ESPHome/ESPHome-MeteringOutlet.groovy'
    ) {

        capability 'Actuator'
        capability 'CurrentMeter'
        capability 'EnergyMeter'
        capability 'PowerMeter'
        capability 'Sensor'
        capability 'VoltageMeasurement'

        command "addChildVue", [[name:"Add a Vue.", type: "STRING", description: "Enter the IP address of the Vue you want to add to this panelboard." ]]
        command "refreshAggregateMeterValues", [[name:"Start Aggregation process.", type: "STRING", description: "Restarts the attribute aggregation process." ]]

        attribute 'frequency', 'number'
        attribute 'aCurrent', 'number'
        attribute 'bCurrent', 'number'
        attribute 'aPower', 'number'
        attribute 'bPower', 'number'
        attribute 'aVoltage', 'number'
        attribute 'bVoltage', 'number'
        attribute 'phaseAngle', 'number'
        attribute 'totalPower', 'number'
        attribute 'balancePower', 'number'
        attribute 'totalDailyEnergy', 'number'
        attribute 'balanceDailyPower', 'number'

    }
/*
    preferences {
        input name: 'password',     // optional setting for API library
                type: 'text',
                title: 'Device Password <i>(if required)</i>',
                required: false

        input name: 'logEnable',    // if enabled the library will log debug details
                type: 'bool',
                title: 'Enable Debug Logging',
                required: false,
                defaultValue: false

        input name: 'logTextEnable',
              type: 'bool',
              title: 'Enable descriptionText logging',
              required: false,
              defaultValue: true
    }  */
}

    

public void installed() {
    log.info "${device} driver installed"
    
    refreshAggregateMeterValues() 
}

public void logsOff() {
    espHomeSubscribeLogs(LOG_LEVEL_INFO, false) // disable device logging
    device.updateSetting('logEnable', false)
    log.info "${device} debug logging disabled"
}

public void updated() {
    log.info "${device} driver configuration updated"
}

public void uninstalled() {
 
    // Remove the attached Vue child devices
    cds = getChildDevices()
    log.info "Removing child ${cds.label}"
    cds.each{netID -> 
        deleteChildDevice(netID.label)
    }
    log.info "${device} driver uninstalled"
}

void addChildVue(ipaddress) {

    boolean vueExists
    cds = getChildDevices()
    if (cds.size() > 0){
        cds.each{childVue ->
            If (childVue.ipaddress == ipaddress){
                vueExists = true
            }}
    }
    if (!vueExists) {
        newChild = addChildDevice("esphome", "ESPHome Emporia Vue", "${device}-Vue${cds.size()+1}", [name: "${device}-Vue${cds.size()+1}", label: "${device}-Vue${cds.size()+1}",isComponent: true])
        newChild.updateSetting("ipAddress", ipaddress)
        newChild.initialize()
    }

}

public void refreshAggregateMeterValues() {

    String type = "Physical"
    tempACurrent = 0
    tempBCurrent = 0
    tempAPower = 0
    tempBPower = 0
    tempTotalPower = 0
    tempBalancePower = 0
    tempTotalDailyEnergy = 0
    tempBalanceDailyEnergy = 0

    List attachedVues = getChildDevices()
    
    if (attachedVues.size() > 0) {
        attachedVues.each { childVue -> 

        log.info "Getting values from -> ${childVue}"
            log.info "Available attributes -> ${childVue.supportedAttributes}"
        log.warn "A Current from ${childVue}: ${childVue.currentValue("aCurrent")}"  
            
            tempACurrent += childVue.currentValue("aCurrent") ?: 0.0
            tempBCurrent += childVue.currentValue("bCurrent") ?: 0.0

            tempAPower += childVue.currentValue("aPower") ?: 0.0
            tempBPower += childVue.currentValue("bPower") ?: 0.0

            tempTotalPower += childVue.currentValue("totalPower") ?: 0.0
            tempBalancePower += childVue.currentValue("balancePower") ?: 0.0

            tempTotalDailyEnergy += childVue.currentValue("totalDailyEnergy") ?: 0.0
            tempBalanceDailyEnergy += childVue.currentValue("balanceDailyEnergy") ?: 0.0  //????? not getting value from child
        }

        String unit = 'A'
        descriptionText = "A current is ${tempACurrent}"
        sendEvent(name: 'aCurrent', value: tempACurrent, unit: unit, type: type, descriptionText: descriptionText)
        if (logTextEnable) { log.info descriptionText }
        descriptionText = "B current is ${tempBCurrent}"
        sendEvent(name: 'bCurrent', value: tempBCurrent, unit: unit, type: type, descriptionText: descriptionText)
        if (logTextEnable) { log.info descriptionText }

        unit = 'W'
        descriptionText = "A power is ${tempAPower}"
        sendEvent(name: 'aPower', value: tempAPower, unit: unit, type: type, descriptionText: descriptionText)
        if (logTextEnable) { log.info descriptionText }
        descriptionText = "B power is ${tempBPower}"
        sendEvent(name: 'bPower', value: tempBPower, unit: unit, type: type, descriptionText: descriptionText)
        if (logTextEnable) { log.info descriptionText }
        descriptionText = "Total power is ${tempTotalPower}"
        sendEvent(name: 'totalPower', value: tempTotalPower, unit: unit, type: type, descriptionText: descriptionText)
        if (logTextEnable) { log.info descriptionText }
        descriptionText = "Balance power is ${tempBalancePower}"
        sendEvent(name: 'balancePower', value: tempBalancePower, unit: unit, type: type, descriptionText: descriptionText)
        if (logTextEnable) { log.info descriptionText }

        unit = 'Wh'
        descriptionText = "Total Daily energy is ${tempTotalDailyEnergy}"
        sendEvent(name: 'totalDailyEnergy', value: tempTotalDailyEnergy, unit: unit, type: type, descriptionText: descriptionText)
        if (logTextEnable) { log.info descriptionText }
        descriptionText = "Balance Daily energy is ${tempBalanceDailyEnergy}"
        sendEvent(name: 'balanceDailyEnergy', value: tempBalanceDailyEnergy, unit: unit, type: type, descriptionText: descriptionText)
        if (logTextEnable) { log.info descriptionText }

        unit = 'V'
        descriptionText = "A voltage is ${(attachedVues[0].currentValue("aVoltage") ?: 0.0)}"
        sendEvent(name: 'aVoltage', value: (attachedVues[0].currentValue("aVoltage") ?: 0.0), unit: unit, type: type, descriptionText: descriptionText)
        if (logTextEnable) { log.info descriptionText }
        descriptionText = "B voltage is ${(attachedVues[0].currentValue("bVoltage") ?: 0.0)}"
        sendEvent(name: 'bVoltage', value: (attachedVues[0].currentValue("bVoltage") ?: 0.0), unit: unit, type: type, descriptionText: descriptionText)
        if (logTextEnable) { log.info descriptionText }

        unit = 'Hz'
        descriptionText = "Frequency is ${(attachedVues[0].currentValue("frequency") ?: 0.0)}"
        sendEvent(name: 'frequency', value: (attachedVues[0].currentValue("frequency") ?: 0.0), unit: unit, type: type, descriptionText: descriptionText)
        if (logTextEnable) { log.info descriptionText }
    
        unit = '°'
        descriptionText = "Phase angle is ${(attachedVues[0].currentValue("phaseAngle") ?: 0.0)}"
        sendEvent(name: 'phaseAngle', value: (attachedVues[0].currentValue("phaseAngle") ?: 0.0), unit: unit, type: type, descriptionText: descriptionText)
        if (logTextEnable) { log.info descriptionText }
    }
    runIn(30, "refreshAggregateMeterValues", [overwrite: true])
}


public void parse(Map message) {
        
}
