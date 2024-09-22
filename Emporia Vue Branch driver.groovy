/**
 *  MIT License
 *  Copyright 2022 Jonathan Bradshaw (jb@nrgup.net)
 *  and Copyright 2024 Wayne Pirtle
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
 
      Version  Comment                Date           By
      -------+----------------------+--------------+-----------
        0.5    Stable Beta           26 Aug 2024    Wayne Pirtle

*/
metadata {
    definition(
        name: 'ESPHome Emporia Vue Branch Circuit',
        namespace: 'esphome',
        author: 'Wayne Pirtle/Jonathan Bradshaw',
        singleThreaded: true,        
        importUrl: 'https://raw.githubusercontent.com/odwp/Vue/main/Emporia%20Vue%20Branch%20driver.groovy'
    ) {

        capability 'Actuator'
        capability 'CurrentMeter'
        capability 'EnergyMeter'
        capability 'PowerMeter'
        capability 'Sensor'
    }

    preferences {
    }
}

public void initialize() {
}

public void installed() {
    log.info "${device} branch circuit driver installed"
}


public void updated() {
}

public void uninstalled() {
    log.info "${device} driver uninstalled"
}



// the parse method is invoked by the API library when messages are received
public void parse(Map tempMsg) {
    
    String type = "Physical"    
    tempMsg.each{key, val -> 
        switch(key) {
            case '_current':
                Float bCurrent = round(val as Float, 1)
                String unit = 'A'
                descriptionText = "Current is ${bCurrent}"
                sendEvent(name: 'amperage', value: bCurrent, unit: unit, type: type, descriptionText: descriptionText)
                if (logTextEnable) { log.info descriptionText }
            break
              
            case '_daily_energy':
                Float bEnergy = round(val as Float, 1)
                String unit = 'Wh'
                descriptionText = "Daily Energy is ${bEnergy}"
                sendEvent(name: 'energy', value: bEnergy, unit: unit, type: type, descriptionText: descriptionText)
                if (logTextEnable) { log.info descriptionText }
            break
              
            case '_power':
                Float aPower = round(val as Float, 1)
                String unit = 'W'
                descriptionText = "Power is ${aPower}"
                sendEvent(name: 'power', value: aPower, unit: unit, type: type, descriptionText: descriptionText)
                if (logTextEnable) { log.info descriptionText }
            break
        }
    }
}

private static float round(float f, int decimals = 0) {
    return new BigDecimal(f).setScale(decimals, java.math.RoundingMode.HALF_UP).floatValue();
}

// Put this line at the end of the driver to include the ESPHome API library helper
#include esphome.espHomeApiHelper
