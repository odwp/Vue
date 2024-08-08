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
        name: 'ESPHome Emporia Vue',
        namespace: 'esphome',
        author: 'Wayne Pirtle/Jonathan Bradshaw',
        singleThreaded: true,
   //     importUrl: 'https://raw.githubusercontent.com/bradsjm/hubitat-drivers/main/ESPHome/ESPHome-MeteringOutlet.groovy'
    ) {

        capability 'Actuator'
        capability 'CurrentMeter'
        capability 'EnergyMeter'
        capability 'PowerMeter'
        capability 'Refresh'
        capability 'Initialize'
        capability 'Sensor'
        capability 'SignalStrength'
        capability 'VoltageMeasurement'

        // attribute populated by ESPHome API Library automatically
        attribute 'networkStatus', 'enum', [ 'connecting', 'online', 'offline' ]
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

    }

    preferences {
        input name: 'ipAddress',    // required setting for API library
                type: 'text',
                title: 'Device IP Address',
                required: true

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
    }
}

public void initialize() {
    // API library command to open socket to device, it will automatically reconnect if needed 
    openSocket()
    state.keys2BranchCts = [:]
    if (logEnable) {
        runIn(1800, 'logsOff')
    }
}

public void installed() {
    log.info "${device} driver installed"
    
    // Create the branch circuit child devices
    for (i=1;i<=16;i++){
        branchChild = Integer.toHexString(i-1)
        log.info "Creating child ${device}-c${branchChild}"
        def cd = getChildDevice("${device}-c${branchChild}")
        if (!cd) {
            cd = addChildDevice("esphome", "ESPHome Emporia Vue Branch Circuit", "${device}-c${branchChild}", [name: "${device}-c${branchChild}",isComponent: true])
        }

    }
    
}

public void logsOff() {
    espHomeSubscribeLogs(LOG_LEVEL_INFO, false) // disable device logging
    device.updateSetting('logEnable', false)
    log.info "${device} debug logging disabled"
}

public void updated() {
    log.info "${device} driver configuration updated"
    initialize()
}

public void uninstalled() {
    closeSocket('driver uninstalled') // make sure the socket is closed when uninstalling
 
    // Remove the branch circuit child devices
    for (i=1;i<=16;i++){
        branchChild = Integer.toHexString(i-1)
        log.info "Removing child ${device}-c${branchChild}"
        def cd = getChildDevice("${device}-c${branchChild}")
//        if (!cd) {
            cd = deleteChildDevice( "${device}-c${branchChild}")
//        }

    }
    log.info "${device} driver uninstalled"
}

// driver commands
public void refresh() {
    log.info "${device} refresh"
//    state.clear()
    state.requireRefresh = true
    espHomeDeviceInfoRequest()
}

// the parse method is invoked by the API library when messages are received
public void parse(Map message) {
    if (logEnable) { log.debug "ESPHome received: ${message}" }
    
    tempKeys2BranchCts = [:]
    
    switch (message.type) {
        case 'device':
            // Device information
        log.info "Device platform -> name: ${message.name}, MAC: ${message.macAddress}"
            state.name = message.name
            state.macAddress = message.macAddress
            break

        case 'entity':

            tempKey = message.key
            tempCkt = message.objectId.substring(0,2)
            tempType = message.objectId.substring(2)
                tempKeys2BranchCts[tempKey] = tempCkt
            if (!state.keys2BranchCts){
                state.keys2BranchCts = [tempKey:[tempCkt:(message.deviceClass)]]
            } else {
                state.keys2BranchCts[(tempKey)] = [(tempCkt):(tempType)]
            }
            break


        case 'state':
        
        // Need to determine if the state is for a device on the main or a branch.
        // for a branch, send it to the branch child to process further, otherwise let it continue here.
        
        // Total Daily Energy. Balance Daily Energy, and d3-led need m_ designation.
        
            // Check if the entity key matches the message entity key received to update device state
        tempKey = message.key
        if(!(message.key==null)){
            tempMap = state.keys2BranchCts.(message.key)
            if(tempMap.containsKey('m_')){
                switch (tempMap.'m_') {
              
                case 'a_current':
                    Float aCurrent = round(message.state as Float, 1)
                    String unit = 'A'
                    descriptionText = "A current is ${aCurrent}"
                    sendEvent(name: 'aCurrent', value: aCurrent, unit: unit, type: type, descriptionText: descriptionText)
                    if (logTextEnable) { log.info descriptionText }
                break
              
                case 'b_current':
                    Float bCurrent = round(message.state as Float, 1)
                    String unit = 'A'
                    descriptionText = "B current is ${bCurrent}"
                    sendEvent(name: 'bCurrent', value: bCurrent, unit: unit, type: type, descriptionText: descriptionText)
                    if (logTextEnable) { log.info descriptionText }
                break
              
                case 'a_voltage':
                    Float aVoltage = round(message.state as Float, 1)
                    String unit = 'V'
                    descriptionText = "A voltage is ${aVoltage}"
                    sendEvent(name: 'aVoltage', value: aVoltage, unit: unit, type: type, descriptionText: descriptionText)
                    if (logTextEnable) { log.info descriptionText }
                break
              
                case 'b_voltage':
                    Float bVoltage = round(message.state as Float, 1)
                    String unit = 'V'
                    descriptionText = "B voltage is ${bVoltage}"
                    sendEvent(name: 'bVoltage', value: bVoltage, unit: unit, type: type, descriptionText: descriptionText)
                    if (logTextEnable) { log.info descriptionText }
                break
              
                case 'a_power':
                    Float aPower = round(message.state as Float, 1)
                    String unit = 'W'
                    descriptionText = "A power is ${aPower}"
                    sendEvent(name: 'aPower', value: aPower, unit: unit, type: type, descriptionText: descriptionText)
                    if (logTextEnable) { log.info descriptionText }
                break
              
                case 'b_power':
                    Float bPower = round(message.state as Float, 1)
                    String unit = 'V'
                    descriptionText = "B voltage is ${bPower}"
                    sendEvent(name: 'bPower', value: bPower, unit: unit, type: type, descriptionText: descriptionText)
                    if (logTextEnable) { log.info descriptionText }
                break
              
                case 'total_power':
                    Float tPower = round(message.state as Float, 1)
                    String unit = 'W'
                    descriptionText = "Total Power is ${tPower}"
                    sendEvent(name: 'totalPower', value: tPower, unit: unit, type: type, descriptionText: descriptionText)
                    if (logTextEnable) { log.info descriptionText }  
                break
              
                case 'balance_power':
                    Float bPower = round(message.state as Float, 1)
                    String unit = 'W'
                    descriptionText = "Balance Power is ${bPower}"
                    sendEvent(name: 'balancePower', value: bPower, unit: unit, type: type, descriptionText: descriptionText)
                    if (logTextEnable) { log.info descriptionText }
                break

                case 'frequency':
                    Float phaseAngle = round(message.state as Float, 1)
                    String unit = 'ø'
                    descriptionText = "Frequency is ${phaseAngle}"
                    sendEvent(name: 'frequency', value: phaseAngle, unit: unit, type: type, descriptionText: descriptionText)
                    if (logTextEnable) { log.info descriptionText }
                break

                case 'phase_angle':
                    Float phaseAngle = round(message.state as Float, 1)
                    String unit = '°'
                    descriptionText = "Phase Angle is ${phaseAngle}"
                    sendEvent(name: 'phaseAngle', value: phaseAngle, unit: unit, type: type, descriptionText: descriptionText)
                    if (logTextEnable) { log.info descriptionText }
                break
              
                case 'total_daily_energy':
                    Float tDayEnergy = round(message.state as Float, 1)
                    String unit = 'Wh'
                    descriptionText = "Total Daily Energy is ${tDayEnergy}"
                    sendEvent(name: 'totalDailyPower', value: tDayEnergy, unit: unit, type: type, descriptionText: descriptionText)
                    if (logTextEnable) { log.info descriptionText }  
                break
              
                case 'balance_daily_power':
                    Float bDayEnergy = round(message.state as Float, 1)
                    String unit = 'Wh'
                    descriptionText = "Balance Daily Energy is ${bDayEnergy}"
                    sendEvent(name: 'balanceDailyPower', value: bDayEnergy, unit: unit, type: type, descriptionText: descriptionText)
                    if (logTextEnable) { log.info descriptionText }
                break

                } 

            } else {
                tempMap.each{key, value ->
                    log.debug "tempMap --> ${tempMap}"
                    log.debug "Retriving key -> ${device}-${key}  value-> ${value}"
                    def cd = getChildDevice("${device}-${key}")
                    Map msg2Child = [(value) : (message.state)]
                    cd.parse(msg2Child)
                }
            }
        }
   }
}
private static float round(float f, int decimals = 0) {
    return new BigDecimal(f).setScale(decimals, java.math.RoundingMode.HALF_UP).floatValue();
}

// Put this line at the end of the driver to include the ESPHome API library helper
#include esphome.espHomeApiHelper
