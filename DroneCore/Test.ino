#include <Servo.h> 

Servo esc;

int escPin = 9;
int minPulseRate = 1000;
int maxPulseRate = 2000;
int throttleChangeDelay = 20;

int curr_throttle;

void setup() {
  
  Serial.begin(9600);
  Serial.setTimeout(500);
  
  Serial.println("Starting Up!");
 
  
  // Attach the the servo to the correct pin and set the pulse range
  esc.attach(escPin, minPulseRate, maxPulseRate); 
  // Write a minimum value (most ESCs require this correct startup)
  esc.write(0);
  curr_throttle = 0;
  delay(2000);
  Serial.println("Ready!");
}

void loop() {
  int throttle = analogRead(0);
  throttle = map(throttle, 0, 1023, 0, 180);
  throttle = normalizeThrottle(throttle);
  if(throttle < 46 && throttle > 10)
  {
    throttle = 46;
  }else if(throttle <= 10)
  {
    throttle = 0;
  }
  if(throttle != curr_throttle)
  {
    Serial.print("Setting throttle to: ");
    Serial.println(throttle);
    curr_throttle = throttle;
    changeThrottle(throttle);
  }

  // Wait for some input
  //if (Serial.available() > 0) {
    
    // Read the new throttle value
    //int throttle = normalizeThrottle( Serial.parseInt() );
    
    // Print it out
    //Serial.print("Setting throttle to: ");
    //Serial.println(throttle);
    
    // Change throttle to the new value
    //changeThrottle(throttle);
    
  //}

}

void changeThrottle(int throttle) {
  
  // Read the current throttle value
  int currentThrottle = readThrottle();
  
  // Are we going up or down?
  int step = 1;
  if( throttle < currentThrottle )
    step = -1;
  
  // Slowly move to the new throttle value 
  while( currentThrottle != throttle ) {
    esc.write(currentThrottle + step);
    currentThrottle = readThrottle();
    delay(throttleChangeDelay);
  }
  
}

int readThrottle() {
  int throttle = esc.read();
  
  Serial.print("Current throttle is: ");
  Serial.println(throttle);
  
  return throttle;
}

// Ensure the throttle value is between 0 - 180
int normalizeThrottle(int value) {
  if( value < 0 )
    return 0;
  if( value > 180 )
    return 180;
return value;
}
