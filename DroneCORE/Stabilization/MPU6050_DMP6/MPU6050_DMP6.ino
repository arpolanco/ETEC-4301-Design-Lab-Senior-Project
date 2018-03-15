#define LEFT_PIN 9
#define RIGHT_PIN 10
#define POT_PIN 0

float elapsedTime = 0;
float curTime = 0; 
float prevTime = 0;

float desiredAngle = 0;

float throttle = 50;//127.0f;
float maxThrottle = 255.0f;
float minThrottle = 1.0f;
float curLeftThrottle = throttle;
float curRightThrottle = throttle;
int i = 0;

void setup() {
  MPUSetup();
  Serial.println("4");
  delay(1000);
  Serial.println("3");
  delay(1000);
  Serial.println("2");
  delay(1000);
  Serial.println("1");
  delay(1000);
  curTime = millis();
  pinMode(A0, INPUT);
}


void loop() {
    prevTime = curTime;
    curTime = millis();
    elapsedTime = (curTime - prevTime) / 1000; 
    throttle = analogRead(POT_PIN)/4;
    Serial.println(throttle);
    MPULoop();
    
    //Serial.println(getYaw());
    float pid = getPID(elapsedTime, getRoll(), desiredAngle);
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
    
    
    //Serial.println(pid);
    //Serial.println(curLeftThrottle);
    //Serial.println(curRightThrottle);
    //Serial.println(" ");
    //Serial.println(i);
    analogWrite(LEFT_PIN, (int)curLeftThrottle);
    analogWrite(RIGHT_PIN, (int)curRightThrottle);
    i++;
    delay(30);
}
