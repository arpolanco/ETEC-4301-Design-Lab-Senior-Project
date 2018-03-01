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
#define MOTOR4_PIN 6

#define NO 0
#define P1 1
#define P2 2
#define P3 3
#define P4 4
#define RUN 5
#define ALL 7

int value = 0; // set values you need to zero
int mode;
char in;

Servo ESC1, ESC2, ESC3, ESC4; //Create as much as Servoobject you want. You can controll 2 or more Servos at the same time

int escPin = 9;
int minPulseRate = 1000;
int maxPulseRate = 2000;
int throttleChangeDelay = 20;

int curr_throttle;

void setup() {
  Serial.begin(9600);    // start serial at 9600 baud
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

  mode = NO;
}

void loop() {
  if(mode == RUN){
    if(Serial.available()) {
      curr_throttle = 0;
      mode = NO;
      ESC1.write(curr_throttle);
      ESC2.write(curr_throttle);
      ESC3.write(curr_throttle);
      ESC4.write(curr_throttle);
    }
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
      curr_throttle = throttle;
      ESC1.write(curr_throttle);
      ESC2.write(curr_throttle);
      ESC3.write(curr_throttle);
      ESC4.write(curr_throttle);
    }

  } else if(mode == NO){
    curr_throttle = 0;
    if(Serial.available()) {
      in = Serial.read();    // Parse an Integer from Serial
      if(in == '1'){
        mode = P1;
        Serial.println("Programming ESC1");
      }else if(in == '2'){
        mode = P2;
        Serial.println("Programming ESC2");
      }else if(in == '3'){
        mode = P3;
        Serial.println("Programming ESC3");
      }else if(in == '4'){
        mode = P4;
        Serial.println("Programming ESC4");
      }else if(in == '5'){
        mode = RUN;
        Serial.println("Running!");
      } else if(in == '7') {
        mode = ALL;
        Serial.println("Programming all!");
      }else {
        Serial.println("Invalid Character!");
      }
    }
  }else{
    switch(mode){
      case P1:
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
            ESC1.write(value);
            Serial.flush();
          }
        }
        break;
      case P2:
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
            ESC2.write(value);
            Serial.flush();
          }
        }
        break;
      case P3:
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
            ESC3.write(value);
            Serial.flush();
          }
        }
        break;
      case P4:
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
            ESC4.write(value);
            Serial.flush();
          }
        }
        break;
      case ALL:
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
            ESC1.write(value);
            ESC2.write(value);
            ESC3.write(value);
            ESC4.write(value);
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
