
#include <Wire.h>
#include <Servo.h>

#define MIN_THROT 1290
#define MAX_THROT 2000
#define ANGLE_BOUND 30
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
double kp_r=0.75;//3.55
double ki_r=0.05;//0.005;//0.003
double kd_r=0.30;//2.05
double kp_y=0.5;//3.55
double ki_y=0.05;//0.005;//0.003
double kd_y=0.2;//2.05
double kp_p=0.75;//3.55
double ki_p=0.05;//0.005;//0.003
double kd_p=0.30;//2.05
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

  if(firing){
    //Serial.print("Firing for ");
    //Serial.println(time_firing);
    time_firing += elapsedTime;
    if(time_firing > FIRE_DURATION){
      time_firing = 0;
      firing = false;
      digitalWrite(LAZAR_PIN, LOW);
    }
  }

  
  /////////////////////////////I M U/////////////////////////////////////
  MPULoop();
  
  if(mode == FLIGHT){
    ///////////////////////////INPUT/////////////////////////////////////
    if(Serial.available() > 0){
      byte input = Serial.read();
      byte masked = input;

      if(input&0x80){
        masked = input&(0x7f);
        throttle = map(masked, 0, 127, MIN_THROT, MAX_THROT);
        Serial.println("Throttle: ");        
      }else if(input&0x40)
      {
        if(input == 'q'){
          Serial.println("QUIT: ");
          mode = TUNING;
          left_front_prop.writeMicroseconds(1000);
          right_front_prop.writeMicroseconds(1000);
          left_back_prop.writeMicroseconds(1000);
          right_back_prop.writeMicroseconds(1000);
            
          Serial.println("Received: ");
          Serial.println(masked);
          return;
        }
        else{
          prySelect = (input & 0x30) >> 4;
          pidSelect = (input & 0x0C) >> 2;
          //if(Serial.available() > 0){
            input = Serial.read();
            PID_tmp = 5.0f * input / 255.0f;
            switch(prySelect)
            {
              case(0): //Pitch
                if(prySelect == 0) //Proportional
                  kp_p = PID_tmp;
                else if(prySelect == 1) //Integral
                  ki_p = PID_tmp / 5.0f;
                else //Differential
                  kd_p = PID_tmp;
              break;
              case(1): //Roll
                if(prySelect == 0) //Proportional
                  kp_r = PID_tmp;
                else if(prySelect == 1) //Integral
                  ki_r = PID_tmp  / 5.0f;
                else //Differential
                  kd_r = PID_tmp;
              break;
              case(2): //Yaw
                if(prySelect == 0) //Proportional
                  kp_y = PID_tmp;
                else if(prySelect == 1) //Integral
                  ki_y = PID_tmp / 5.0f;
                else //Differential
                  kd_y = PID_tmp;
              break;
              default: //Even more uh-oh
                Serial.println("You shouldn't see this...(Wrong PRY value)");
              break;
              
            //}
          }
          //Reset I value in case I is set to zero to avoid weird bugs
          pid_i_r = 0;
          pid_i_p = 0;
          pid_i_y = 0;
        }
      }else if(input == 0x30)
      {
        Serial.println("FIRE: ");
        digitalWrite(LAZAR_PIN, HIGH);
        firing = true;
      }else{
        Serial.println("Flight Value: ");
        masked = input&0x0f;
        flight_values[(input&0x30)>>6] = masked;
      }
      
      Serial.println("Received: ");
      Serial.println(masked);
      while(Serial.available() > 0){
        Serial.read();
      }
      
      Serial.flush();
    }

    if(!hit_detected){
      if(digitalRead(HIT_1) == LOW || digitalRead(HIT_2) == LOW || digitalRead(HIT_3) == LOW || digitalRead(HIT_4) == LOW){
        hit_detected = true;
      }
    }else{
      if(digitalRead(HIT_1) || digitalRead(HIT_2) || digitalRead(HIT_3) || digitalRead(HIT_4)){
        Serial.println("Hit Detected!");
        hit_detected = false;
      }
    }
  
    /*///////////////////////////P I D///////////////////////////////////*/
    /*Remember that for the balance we will use just one axis. I've choose the x angle
    to implement the PID with. That means that the x axis of the IMU has to be paralel to
    the balance*/
    
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
    
        
    PID_r = get_pid(kp_r, ki_r, kd_r, &pid_i_r, error_r, &previous_error_r);
    PID_p = get_pid(kp_p, ki_p, kd_p, &pid_i_p, error_p, &previous_error_p);
    PID_y = get_pid(kp_y, ki_y, kd_y, &pid_i_y, error_y, &previous_error_y);
    
    //throttle = analogRead(0);
    //throttle = map(throttle, 0, 1023, MIN_THROT, MAX_THROT);
    
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

    //Serial.print("  Throt: ");
    //Serial.print(throttle);

    //Serial.print("  P: ");
    //Serial.print(desired_angle_p);

    //Serial.print("  R: ");
    //Serial.print(desired_angle_r);

    //Serial.print("  dY: ");
    //Serial.print(delta_yaw);
    
    //Serial.print("  Y: ");
    //Serial.println(desired_angle_y);
        
    /*Finaly using the servo function we create the PWM pulses with the calculated
    width for each pulse*/
    left_front_prop.writeMicroseconds(pwmLF);
    right_front_prop.writeMicroseconds(pwmRF);
    left_back_prop.writeMicroseconds(pwmLB);
    right_back_prop.writeMicroseconds(pwmRB);
  }else{
    if(Serial.available() > 0){
      char input = Serial.read();
      if(input == 'p')
      {
        //Serial.println("Entering Flight Mode!");
        mode = FLIGHT;
        Serial.println("FlightMode!");
        r_setpoint = getRoll();
        p_setpoint = getPitch();
        y_setpoint = getYaw();
        throttle = MIN_THROT;
        flight_values[0] = 7;
        flight_values[1] = 7;
        flight_values[2] = 7;
        
      }
      if(input == 'k'){
        //k change mode
        input = Serial.read();
        if(input == 'p'){
          //pitch
          input = Serial.read();
        } else if(input == 'r'){
          //roll
        } else if(input == 'y'){
          //pitch
        }
      }
      Serial.println("Received: ");
      Serial.println(input);
    }
  }
}//end of loop void

/*
  States:
    Flight Mode
      Motors are on, can accept input which only changes Pitch, Roll, Yaw, and or Firing
    Idle Mode
      Motors K values can be tuned and tested

*/


/*
  /*The tiemStep is the time that elapsed since the previous loop. 
   * This is the value that we will use in the formulas as "elapsedTime" 
   * in seconds. We work in ms so we haveto divide the value by 1000 
   to obtain seconds*/

  /*Reed the values that the accelerometre gives.
   * We know that the slave adress for this IMU is 0x68 in
   * hexadecimal. For that in the RequestFrom and the 
   * begin functions we have to put this value.*/
   
     //Wire.beginTransmission(0x68);
     //Wire.write(0x3B); //Ask for the 0x3B register- correspond to AcX
     //Wire.endTransmission(false);
     //Wire.requestFrom(0x68,6,true); 
   
   /*We have asked for the 0x3B register. The IMU will send a brust of register.
    * The amount of register to read is specify in the requestFrom function.
    * In this case we request 6 registers. Each value of acceleration is made out of
    * two 8bits registers, low values and high values. For that we request the 6 of them  
    * and just make then sum of each pair. For that we shift to the left the high values 
    * register (<<) and make an or (|) operation to add the low values.*/
    
     //Acc_rawX=Wire.read()<<8|Wire.read(); //each value needs two registres
     //Acc_rawY=Wire.read()<<8|Wire.read();
     //Acc_rawZ=Wire.read()<<8|Wire.read();

 
    /*///This is the part where you need to calculate the angles using Euler equations///*/
    
    /* - Now, to obtain the values of acceleration in "g" units we first have to divide the raw   
     * values that we have just read by 16384.0 because that is the value that the MPU6050 
     * datasheet gives us.*/
    /* - Next we have to calculate the radian to degree value by dividing 180º by the PI number
    * which is 3.141592654 and store this value in the rad_to_deg variable. In order to not have
    * to calculate this value in each loop we have done that just once before the setup void.
    */

    /* Now we can apply the Euler formula. The atan will calculate the arctangent. The
     *  pow(a,b) will elevate the a value to the b power. And finnaly sqrt function
     *  will calculate the rooth square.*/
     /*---X---*/
     //Acceleration_angle[0] = atan((Acc_rawY/16384.0)/sqrt(pow((Acc_rawX/16384.0),2) + pow((Acc_rawZ/16384.0),2)))*rad_to_deg;
     /*---Y---*/
     //Acceleration_angle[1] = atan(-1*(Acc_rawX/16384.0)/sqrt(pow((Acc_rawY/16384.0),2) + pow((Acc_rawZ/16384.0),2)))*rad_to_deg;
 
   /*Now we read the Gyro data in the same way as the Acc data. The adress for the
    * gyro data starts at 0x43. We can see this adresses if we look at the register map
    * of the MPU6050. In this case we request just 4 values. W don¡t want the gyro for 
    * the Z axis (YAW).*/
    
   //Wire.beginTransmission(0x68);
   //Wire.write(0x43); //Gyro data first adress
   //Wire.endTransmission(false);
   //Wire.requestFrom(0x68,4,true); //Just 4 registers
   
   //Gyr_rawX=Wire.read()<<8|Wire.read(); //Once again we shif and sum
   //Gyr_rawY=Wire.read()<<8|Wire.read();
 
   /*Now in order to obtain the gyro data in degrees/seconda we have to divide first
   the raw value by 131 because that's the value that the datasheet gives us*/

   /*---X---*/
   //Gyro_angle[0] = Gyr_rawX/131.0; 
   /*---Y---*/
   //Gyro_angle[1] = Gyr_rawY/131.0;

   /*Now in order to obtain degrees we have to multiply the degree/seconds
   *value by the elapsedTime.*/
   /*Finnaly we can apply the final filter where we add the acceleration
   *part that afects the angles and ofcourse multiply by 0.98 */

   /*---X axis angle---*/
   //Total_angle[0] = 0.98 *(Total_angle[0] + Gyro_angle[0]*elapsedTime) + 0.02*Acceleration_angle[0];
   /*---Y axis angle---*/
   //Total_angle[1] = 0.98 *(Total_angle[1] + Gyro_angle[1]*elapsedTime) + 0.02*Acceleration_angle[1];
   
   /*Now we have our angles in degree and values from -10º0 to 100º aprox*/
    ////Serial.println(Total_angle[1]);
    
    //kp_r=analogRead(1);
//kp_r = map(kp_r, 0, 627, 0, 400);
//kp_r /= 100.0f;

//ki_r=analogRead(2);
//ki_r = map(ki_r, 0, 627, 0, 100);
//ki_r /= 100.0f;

//kd_r=analogRead(3);
//kd_r = map(kd_r, 0, 627, 0, 400);
//kd_r /= 100.0f;
