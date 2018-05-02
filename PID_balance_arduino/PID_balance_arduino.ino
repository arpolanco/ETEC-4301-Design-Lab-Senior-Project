
#include <Wire.h>
#include <Servo.h>

#define ZERO_THROT 1000
#define MIN_THROT 1290
#define MAX_THROT 2000
#define ANGLE_BOUND 15
#define MAX_DELTA_YAW 30

#define LAZAR_PIN 4
#define FIRE_DURATION 1 //0.025
#define HIT_1 5
#define HIT_2 6
#define HIT_3 7
#define HIT_4 8

#define LF 3
#define RF 9
#define LB 10
#define RB 11

#define TUNING 0
#define FLIGHT 1
#define PIDTUNE 2

Servo right_front_prop;
Servo left_front_prop;
Servo left_back_prop;
Servo right_back_prop;

/*MPU-6050 gives you 16 bits data so you have to create some 16int constants
 * to store the data for accelerations and gyro*/

char mode;

int16_t Acc_rawX, Acc_rawY, Acc_rawZ,Gyr_rawX, Gyr_rawY, Gyr_rawZ;
 

float Acceleration_angle[2];
float Gyro_angle[2];
float Total_angle[2];

char input_buffer[32];

//Lazer Tag variables
bool firing;
float time_firing;
bool hit_detected;

float elapsedTime, time, timePrev;
int i;
float rad_to_deg = 180/3.141592654;

float PID_tmp = 0;
byte prySelect = 0;
byte pidSelect = 0;

float PID_r, PID_p, PID_y, pwmLF, pwmRF, pwmLB, pwmRB, error_r, error_y, error_p, previous_error_r, previous_error_y, previous_error_p;

/////////////////PID Variables/////////////////
float pid_i_r=0;
float pid_i_y=0;
float pid_i_p=0;

/////////////////PID CONSTANTS/////////////////
double kp_r=2.75;//3.55
double ki_r=0.005;//0.005;//0.003
double kd_r=2.75;//2.05
double kp_y=2.75;//3.55
double ki_y=0.005;//0.005;//0.003
double kd_y=2.75;//2.05
double kp_p=2.75;//3.55
double ki_p=0.005;//0.005;//0.003
double kd_p=2.75;//2.05
///////////////////////////////////////////////

double throttle=MIN_THROT; //initial value of throttle to the motors
float r_setpoint = 0;
float p_setpoint = 0;
float y_setpoint = 0;
float flight_values[] = {7, 7, 7}; //This contains the angles that we want everything set to
float desired_angle_p = 0;
float desired_angle_r = 0;
float desired_angle_y = 0;
float delta_yaw = 0;

char S[32];

void setup() {
  Serial.begin(115200);
  //Serial.println("Starting up...\n");
  
  right_front_prop.attach(9); //attatch the right motor to pin 3
  left_front_prop.attach(3);  //attatch the left motor to pin 5
  right_back_prop.attach(11); //attatch the right motor to pin 3
  left_back_prop.attach(10);  //attatch the left motor to pin 5

  time = millis(); //Start counting time in milliseconds
  /*In order to start up the ESCs we have to send a min value
   * of PWM to them before connecting the battery. Otherwise
   * the ESCs won't start up or enter in the configure mode.
   * The min value is 1000us and max is MAX_THROTus, REMEMBER!*/

  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(LAZAR_PIN, OUTPUT);
  pinMode(HIT_1, INPUT);
  pinMode(HIT_2, INPUT);
  pinMode(HIT_3, INPUT);
  pinMode(HIT_4, INPUT);
  digitalWrite(LED_BUILTIN, LOW);

  left_back_prop.writeMicroseconds(1000); 
  right_back_prop.writeMicroseconds(1000);
  left_front_prop.writeMicroseconds(1000); 
  right_front_prop.writeMicroseconds(1000);

  MPUSetup();

  digitalWrite(LED_BUILTIN, HIGH);
  mode = TUNING;
  Serial.println("Mode = TUNING\n");
  firing = false;
  time_firing = 0;
  hit_detected = false;
  
}//end of setup void


void loop() {
  //////////////////////////////TIMING///////////////////////////////////
  timePrev = time;  // the previous time is stored before the actual time read
  time = millis();  // actual time read
  elapsedTime = (time - timePrev) / 1000; 

  //Turn off lazar after a short time
  if(firing){
    time_firing += elapsedTime;
    if(time_firing > FIRE_DURATION){
      time_firing = 0;
      firing = false;
      digitalWrite(LAZAR_PIN, LOW);
    }
  }

  //Hit detection
  //if(!hit_detected){
  //  if(digitalRead(HIT_1) == LOW || digitalRead(HIT_2) == LOW || digitalRead(HIT_3) == LOW || digitalRead(HIT_4) == LOW){
  //    hit_detected = true;
  //  }
  //}else{
  //  if(digitalRead(HIT_1) || digitalRead(HIT_2) || digitalRead(HIT_3) || digitalRead(HIT_4)){
  //    Serial.println("Hit Detected!");
  //    hit_detected = false;
  //  }
  //}

  //Receive commands from controller
  if(Serial.available() > 0){
    byte input = Serial.read();
    byte masked = input;

    if(input == 0xff){
      if(mode != TUNING){
        Serial.println("The Sever must have broken!");
        Serial.println("Idle Mode: ");
        mode = TUNING;
        left_front_prop.writeMicroseconds(ZERO_THROT);
        right_front_prop.writeMicroseconds(ZERO_THROT);
        left_back_prop.writeMicroseconds(ZERO_THROT);
        right_back_prop.writeMicroseconds(ZERO_THROT);
        
        pid_i_r = 0;
        pid_i_p = 0;
        pid_i_y = 0;
        previous_error_r = 0;
        previous_error_y = 0;
        previous_error_p = 0;
      }
    }else if(input&0x80){
      masked = input&(0x7f);
      throttle = map(masked, 0, 126, MIN_THROT, MAX_THROT);
      Serial.print("Throttle: ");
      Serial.println(throttle);        
    }else if(input&0x40){ //q, p, or k value setting
      if(mode == FLIGHT && input == 'q'){
        Serial.println("Idle Mode: ");
        mode = TUNING;
        left_front_prop.writeMicroseconds(ZERO_THROT);
        right_front_prop.writeMicroseconds(ZERO_THROT);
        left_back_prop.writeMicroseconds(ZERO_THROT);
        right_back_prop.writeMicroseconds(ZERO_THROT);
        
        pid_i_r = 0;
        pid_i_p = 0;
        pid_i_y = 0;
        previous_error_r = 0;
        previous_error_y = 0;
        previous_error_p = 0;
        
      }else if(mode == TUNING && input == 'p'){
        mode = FLIGHT;
        Serial.println("FlightMode!");
        r_setpoint = getRoll();
        p_setpoint = getPitch();
        y_setpoint = getYaw();
        throttle = MIN_THROT;
        flight_values[0] = 7;
        flight_values[1] = 7;
        flight_values[2] = 7;
        
      }else if(input == 'p' || input == 'q'){
        Serial.println("State Remaining the same!");
      }else{
        prySelect = (input & 0x30) >> 4;
        pidSelect = (input & 0x0C) >> 2;

        if(prySelect >= 0 && prySelect < 3 && pidSelect >= 0 && pidSelect < 3){
          while(!Serial.available()){
            ;
          }
          
          Serial.print("Setting K");
          
          input = Serial.read();
          PID_tmp = 5.0f * input / 255.0f;
          switch(prySelect)
          {
            case(0): //Pitch
              Serial.print(" Pitch ");
              if(pidSelect == 0){ //Proportional
                Serial.print("Proportional ");
                kp_p = PID_tmp;
              }else if(pidSelect == 1){ //Integral
                Serial.print("Integral ");
                ki_p = PID_tmp / 5.0f;
              }else{ //Differential
                Serial.print("Differential ");
                kd_p = PID_tmp;
              }
              break;
            case(1): //Roll
              Serial.print(" Roll ");
              if(pidSelect == 0){ //Proportional
                Serial.print("Proportional ");
                kp_r = PID_tmp;
              }else if(pidSelect == 1){ //Integral
                Serial.print("Integral ");
                ki_r = PID_tmp  / 5.0f;
              }else{ //Differential
                Serial.print("Differential ");
                kd_r = PID_tmp;
              }
              break;
            case(2): //Yaw
              Serial.print(" Yaw ");
              if(pidSelect == 0){ //Proportional
                Serial.print("Proportional ");
                kp_y = PID_tmp;
              }else if(pidSelect == 1){ //Integral
                Serial.print("Integral ");
                ki_y = PID_tmp / 5.0f;
              }else{ //Differential
                Serial.print("Differential ");
                kd_y = PID_tmp;
              }
              break;
            default: //Even more uh-oh
              Serial.println("You shouldn't see this...(Wrong PRY value)");
            break;
          }
  
          Serial.println(PID_tmp);
          //Reset I value in case I is set to zero to avoid weird bugs
          pid_i_r = 0;
          pid_i_p = 0;
          pid_i_y = 0;
        }else{
          Serial.println("You shouldn't see this...(Wrong PRY value)");
        }
      }
    }else if(input == 0x30)
    {
      Serial.println("FIRE: ");
      digitalWrite(LAZAR_PIN, HIGH);
      firing = true;
    }else{
      //Serial.print("Flight Value: ");
      //Serial.println((int)(input&0x30)>>4);
      masked = input&0x0f;
      flight_values[(input&0x30)>>4] = masked;
      if((int)((input&0x30)>>4) == 0){
        Serial.print("PITCH SET: ");
        Serial.println(masked);
      }else if((int)((input&0x30)>>4) == 1){
        Serial.print("ROLL SET: ");
        Serial.println(masked);
      }else if((int)((input&0x30)>>4) == 2){
        Serial.print("YAW SET: ");
        Serial.println(masked);
      }else{
        Serial.println("WrongFlightValue!");
      }
    }

    //Make sure we read everything sent already
    while(Serial.available() > 0){
      //Serial.print("Garbage: ");
      //Serial.print(Serial.read());
      //Serial.println("");
      Serial.read();
    }
    Serial.flush();

    //Serial.println("Received: ");
    //Serial.println(input);
  }
  /////////////////////////////I M U/////////////////////////////////////
  MPULoop(elapsedTime);
  
  if(mode == FLIGHT){ 
    /*///////////////////////////P I D///////////////////////////////////*/
    /*   
    /*First calculate the error between the desired angle and 
    *the real measured angle*/
    float true_r = (r_setpoint - getRoll());
    float true_p = (p_setpoint - getPitch());
    float true_y = (y_setpoint - getYaw());
    
    //calculate desired angles
    desired_angle_p = map(flight_values[0], 0, 14, -ANGLE_BOUND, ANGLE_BOUND);
    desired_angle_r = map(flight_values[1], 0, 14, -ANGLE_BOUND, ANGLE_BOUND);
    delta_yaw = map(flight_values[2], 0, 14, -MAX_DELTA_YAW, MAX_DELTA_YAW);
    desired_angle_y += delta_yaw*elapsedTime;
    while(desired_angle_y >= 360)
    {
      desired_angle_y -= 360; 
    }
    while(desired_angle_y < 0)
    {
      desired_angle_y += 360; 
    }
    
    error_r = true_r - desired_angle_r;
    error_p = true_p - desired_angle_p;

    float theta_2 = desired_angle_y + 360;
    float theta_3 = desired_angle_y - 360;

    float err1_y = true_y - theta_2;
    float err2_y = true_y - theta_3;
    error_y = true_y - desired_angle_y;

    if(abs(err1_y) < abs(error_y)){
      error_y = err1_y;
    }
    if(abs(err2_y) < abs(error_y)) {
      error_y = err2_y;
    }

    if(abs(error_r) > 40 || abs(error_p) > 40){
      mode = TUNING;     
      left_front_prop.writeMicroseconds(ZERO_THROT);
      right_front_prop.writeMicroseconds(ZERO_THROT);
      left_back_prop.writeMicroseconds(ZERO_THROT);
      right_back_prop.writeMicroseconds(ZERO_THROT);
      
      pid_i_r = 0;
      pid_i_p = 0;
      pid_i_y = 0;
      previous_error_r = 0;
      previous_error_y = 0;
      previous_error_p = 0;
      return;
    }
    
    //Calculate PID Error Values
    PID_r = get_pid(kp_r, ki_r, kd_r, &pid_i_r, error_r, &previous_error_r);
    PID_p = get_pid(kp_p, ki_p, kd_p, &pid_i_p, error_p, &previous_error_p);
    PID_y = get_pid(kp_y, ki_y, kd_y, &pid_i_y, error_y, &previous_error_y);
    
    ///ROLL
    pwmLF = throttle + PID_r;
    pwmRF = throttle - PID_r;
    pwmLB = throttle + PID_r;
    pwmRB = throttle - PID_r;
    
    //PITCH
    pwmLF += PID_p;
    pwmRF += PID_p;
    pwmLB -= PID_p;
    pwmRB -= PID_p;
    
    //YAW
    pwmLF += PID_y;
    pwmRF -= PID_y;
    pwmLB -= PID_y;
    pwmRB += PID_y;
    
    //RF
    if(pwmRF < MIN_THROT)
    {
      pwmRF= MIN_THROT;
    }
    if(pwmRF > MAX_THROT)
    {
      pwmRF=MAX_THROT;
    }
    //LF
    if(pwmLF < MIN_THROT)
    {
      pwmLF= MIN_THROT;
    }
    if(pwmLF > MAX_THROT)
    {
      pwmLF=MAX_THROT;
    }
    //RB
    if(pwmRB < MIN_THROT)
    {
      pwmRB = MIN_THROT;
    }
    if(pwmRB > MAX_THROT)
    {
      pwmRB =MAX_THROT;
    }
    //LB
    if(pwmLB < MIN_THROT)
    {
      pwmLB= MIN_THROT;
    }
    if(pwmLB > MAX_THROT)
    {
      pwmLB =MAX_THROT;
    }
        
    /*Finaly using the servo function we create the PWM pulses with the calculated
    width for each pulse*/
    left_front_prop.writeMicroseconds(pwmLF);
    right_front_prop.writeMicroseconds(pwmRF);
    left_back_prop.writeMicroseconds(pwmLB);
    right_back_prop.writeMicroseconds(pwmRB);
  }
}//end of loop void

