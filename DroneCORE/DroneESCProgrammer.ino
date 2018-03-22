/*Introduction: ESC Programming on Arduino (Hobbyking ESC)
Picture of ESC Programming on Arduino (Hobbyking ESC)

Hello Community,

I'm going to show you, how to program and use the Hobbyking ESC. I just found a few information and tutorials, which really didn't help me very much, so i decided to program an own sketch, which is very simple to understand.

Important to know:

* ESC means Electronic Speed control
* The ESC has a 5v(not used), GND and Signal Pin like a Servo
* You control it like a Servo with  write() http://arduino.cc/de/Reference/ServoWrite
* or writeMicroseconds  http://arduino.cc/de/Reference/ServoWriteMicroseconds

In my Example i use writeMicroseconds, because it is easier to understand.
So let's go...
Step 1: Getting ESC Information
Picture of Getting ESC Information

You really should remark the Amperevalue of your ESC. This tutorial is only tested on 20 AMP ESC :
http://www.hobbyking.com/hobbyking/store/__15202__hobby_king_20a_esc_3a_ubec.html

I can't really promise, that this is working with an other ESC but i think so, because in the english manual are 20 and 30 Amp ESC's described. In the German version is a generalisation from 10 to 120 Amp, thats why I think this could work for every ESC.

Source:
German: http://tom-620.bplaced.net/rc_modelle/zubehoer/regler/hobby_king/hk_80A_regler_deutsch.pdf
English: http://www.hobbyking.com/hobbyking/store/uploads/811103388X7478X20.pdf
Step 2: Connection to Arduino
Picture of Connection to Arduino

I tried it with an arduino uno R3. I think it's also possible with an e.g. arduino Duemilanove or Mega.

First you have to connect the ESC to you lipo or NiMH. When you have done that you connect the ESC like so:

*  Black to GND
* White/Yellow to PIN 9


Because you connected the ESC to your battery, the ESC is put under voltage. Thats why it It is important, that you DON'T connect the red wire to your 5v Port, because it could destroy your computer's USB Port.

On this picture you can see the correct connection between ESC and Arduino (Mega).

Picture source: http://1.bp.blogspot.com/-eqDaRgO5FjU/T9U3avwT2-I/AAAAAAAAALE/-8pj4qD12Q0/s1600/Figure2_2_edit.jpg
Step 3: Upload Sketch
Picture of Upload Sketch

Just copy and paste this Code to your IDE:

Coded by Marjan Olesch
Sketch from Insctructables.com
Open source - do what you want with this code!
*/
#include <Servo.h>

#define MOTOR1_PIN 9
#define MOTOR2_PIN 11
#define MOTOR3_PIN 10
#define MOTOR4_PIN 3

//#define LEFT_SIDE (MOTOR2_PIN | MOTOR3_PIN)

#define NO 0
#define P1 1
#define P2 2
#define P3 3
#define P4 4
#define RUN 5
#define ALL 7
#define MASK 8

#define ESC1_BIT 1
#define ESC2_BIT 2
#define ESC3_BIT 4
#define ESC4_BIT 8
#define ALL_BIT (1|2|4|8)

int value = 0; // set values you need to zero
int mode;
char in;
unsigned char curBitmask;

Servo ESC1, ESC2, ESC3, ESC4; //Create as much as Servoobject you want. You can controll 2 or more Servos at the same time

int escPin = 9;
int minPulseRate = 1000;
int maxPulseRate = 2000;
int throttleChangeDelay = 20;

int curr_throttle;

void setESCThrottle(unsigned char bitmask, unsigned int value)
{
  //Serial.print(bitmask);
  //Serial.print(" ");
  //Serial.println(value);
  if(bitmask & 0x01){
    //Serial.println("Writing to 1");
    ESC1.write((int)(value * biasESC1));
  }
  if(bitmask & 0x02){
    ESC2.write((int)(value * biasESC2));
    //Serial.println("Writing to 2");
  }
  if(bitmask & 0x04){
    ESC3.write((int)(value * biasESC3));
    //Serial.println("Writing to 3");
  }
  if(bitmask & 0x08){
    ESC4.write((int)(value * biasESC4));
    //Serial.println("Writing to 4");
  }
}

void initializeESCs() {
  //Serial.setTimeout(500);
  Serial.println("Starting Up!");
  
  ESC1.attach(MOTOR_1, minPulseRate, maxPulseRate);    // attached to pin 9 I just do this with 1 Servo
  ESC2.attach(MOTOR_2, minPulseRate, maxPulseRate);    // attached to pin 9 I just do this with 1 Servo
  ESC3.attach(MOTOR_3, minPulseRate, maxPulseRate);    // attached to pin 9 I just do this with 1 Servo
  ESC4.attach(MOTOR_4, minPulseRate, maxPulseRate);    // attached to pin 9 I just do this with 1 Servo

  ESC1.write(0);
  ESC2.write(0);
  ESC3.write(0);
  ESC4.write(0);
  curr_throttle = 0;
  
  delay(2000);
  Serial.println("Ready to Program!");
}


/*
  void programmerSetup() {
  Serial.begin(38400);    // start serial at 9600 baud
  Serial.setTimeout(500);
  Serial.println("Starting Up!");
  
  ESC1.attach(MOTOR1_PIN, minPulseRate, maxPulseRate);    // attached to pin 9 I just do this with 1 Servo
  ESC2.attach(MOTOR2_PIN, minPulseRate, maxPulseRate);    // attached to pin 9 I just do this with 1 Servo
  ESC3.attach(MOTOR3_PIN, minPulseRate, maxPulseRate);    // attached to pin 9 I just do this with 1 Servo
  ESC4.attach(MOTOR4_PIN, minPulseRate, maxPulseRate);    // attached to pin 9 I just do this with 1 Servo

  ESC1.write(0);
  ESC2.write(0);
  ESC3.write(0);
  ESC4.write(0);
  curr_throttle = 0;
  
  delay(2000);
  Serial.println("Ready to Program!");
  ESC4.write(55);
  delay(1000);
  ESC4.write(0);
  mode = NO;
}
*/
void programmerLoop() {
  bool isDone = false;
  while(!isDone){
  int throttle;
  switch(mode){
    case RUN:
      if(Serial.available()) {
        curr_throttle = 0;
        mode = NO;
        setESCThrottle(ALL_BIT, curr_throttle);
      }
      throttle = analogRead(0);
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
        curr_throttle = throttle;
        setESCThrottle(ALL_BIT, curr_throttle);
      }
    break;
    case NO:
      curr_throttle = 0;
      curBitmask = 0;
      while(Serial.available()) {
        in = Serial.peek();
        if(in == 'q'){
          isDone = true;
          break;
        }
        int inNum = in - '0';
        if(inNum > 0 && inNum <= 4) {
          Serial.read();
          unsigned char maskVal = 1;//(unsigned char)pow(2, inNum-1);
          for(int i = 0; i < inNum - 1; i++)
          {
            maskVal *= 2;
          }
          curBitmask |= maskVal;
          Serial.print("ESC");
          Serial.print(inNum);
          Serial.print(" ");
        } else if(in == '|') {
          Serial.read();
        } else {
          Serial.read();
          break;
        }
        delayMicroseconds(3000);
      }
      if(curBitmask != 0){
        Serial.print("\n");
        mode = MASK;
      }
      Serial.flush();
    break;
    case MASK:
      while(1){
        if(Serial.available()){
          in = Serial.peek();
          if(in == 'q'){
            Serial.read();
            mode = NO;
            Serial.println("Ready to Program!");
            break;
          }
          value = Serial.parseInt();
          Serial.println("Writing: ");
          Serial.println(value);
          setESCThrottle(curBitmask, value);
          Serial.flush();
        }
      }
      break;
    default:
      mode = NO;
      break;
  }
  }
}

// Ensure the throttle value is between 0 - 180
int normalizeThrottle(int value) {
  if( value < 0 )
    return 0;
  if( value > 180 )
    return 180;
return value;
}

