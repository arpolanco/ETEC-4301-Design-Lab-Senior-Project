#define POT_PIN 0
#define KP_PIN 1
#define KI_PIN 2
#define KD_PIN 3

float elapsedTime = 0;
float curTime = 0; 
float prevTime = 0;

float desiredAngle = 0.001;

float throttle = 70.0f;//127.0f;
float maxThrottle = 90.0f;
float minThrottle = 60.0f;
float curLeftThrottle = throttle;
float curRightThrottle = throttle;
int i = 0;
float voltageModifier = 5.0f/3.3f;
float curRoll = 0;


void stabSetup() {
  MPUSetup();
  Serial.println("4");
  delay(1000);
  Serial.println("3");
  delay(1000);
  Serial.println("2");
  delay(1000);
  Serial.println("1");
  delay(1000);
  MPULoop();
  desiredAngle = getRoll();
  Serial.print("Desired angle: ");
  Serial.println(desiredAngle);
  curTime = millis();
  pinMode(A0, INPUT);
  setESCThrottle(ESC2_BIT | ESC3_BIT | ESC1_BIT | ESC4_BIT, 90);
  delay(100);
  setESCThrottle(ESC2_BIT | ESC3_BIT | ESC1_BIT | ESC4_BIT, throttle);
}


void stabLoop() {
    prevTime = curTime;
    curTime = millis();
    elapsedTime = (curTime - prevTime) / 1000; 
    //throttle = analogRead(POT_PIN)/4;
    //Serial.print("Throttle: ");
    //Serial.println(throttle);
    MPULoop();
    kp = analogRead(KP_PIN) / 1024.0f * 5 * voltageModifier;
    ki = analogRead(KI_PIN) / 1024.0f * 1 * voltageModifier;
    kd = analogRead(KD_PIN) / 1024.0f * 5 * voltageModifier;
    //Serial.println(getYaw());
    curRoll = getRoll();
    float pid = getPID(elapsedTime, curRoll, desiredAngle);
    if(desiredAngle == 0.001)
    {
      desiredAngle = curRoll;
    }
    //Serial.print("PID: ");
    //Serial.println(pid);
    curLeftThrottle = throttle + pid;
    curRightThrottle = throttle - pid;
    if(curLeftThrottle < minThrottle){
      curLeftThrottle = minThrottle;
    }
    if(curLeftThrottle > maxThrottle){
      curLeftThrottle = maxThrottle;
    }
    if(curRightThrottle < minThrottle){
      curRightThrottle = minThrottle;
    }
    if(curRightThrottle > maxThrottle){
      curRightThrottle = maxThrottle;
    }
    if(curLeftThrottle < 0){
      curLeftThrottle = 0;
    }
    if(curRightThrottle < 0){
      curRightThrottle = 0;
    }
    
    /*
    Serial.print("Right: ");
    Serial.println(curRightThrottle);
    Serial.print("| Leftt: ");
    Serial.println(curLeftThrottle);
    */
    
    //Serial.println(pid);
    //Serial.println(curLeftThrottle);
    //Serial.println(curRightThrottle);
    //Serial.println(" ");
    //Serial.println(i);
    //analogWrite(LEFT_PIN, (int)curLeftThrottle);
    //analogWrite(RIGHT_PIN, (int)curRightThrottle);
    setESCThrottle(ESC2_BIT | ESC3_BIT, int(curRightThrottle));
    setESCThrottle(ESC1_BIT | ESC4_BIT, int(curLeftThrottle));
    i++;
    delay(30);
}

