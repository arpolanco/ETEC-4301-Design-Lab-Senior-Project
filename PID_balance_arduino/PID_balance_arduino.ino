
#include <Wire.h>
#include <Servo.h>


Servo right_front_prop;
Servo left_front_prop;
Servo left_back_prop;
Servo right_back_prop;

/*MPU-6050 gives you 16 bits data so you have to create some 16int constants
 * to store the data for accelerations and gyro*/

int16_t Acc_rawX, Acc_rawY, Acc_rawZ,Gyr_rawX, Gyr_rawY, Gyr_rawZ;
 

float Acceleration_angle[2];
float Gyro_angle[2];
float Total_angle[2];




float elapsedTime, time, timePrev;
int i;
float rad_to_deg = 180/3.141592654;

float PID_r, PID_p, PID_y, pwmLF, pwmRF, pwmLB, pwmRB, error_r, error_y, error_p, previous_error_r, previous_error_y, previous_error_p;
float pid_p_r=0;
float pid_i_r=0;
float pid_d_r=0;
float pid_p_y=0;
float pid_i_y=0;
float pid_d_y=0;
float pid_p_p=0;
float pid_i_p=0;
float pid_d_p=0;

/////////////////PID CONSTANTS/////////////////
double kp_r=0.61;//3.55
double ki_r=0.05;//0.005;//0.003
double kd_r=0.21;//2.05
double kp_y=3.55;//3.55
double ki_y=0;//0.005;//0.003
double kd_y=2.05;//2.05
double kp_p=3.55;//3.55
double ki_p=0;//0.005;//0.003
double kd_p=2.05;//2.05
///////////////////////////////////////////////

double throttle=1300; //initial value of throttle to the motors
float desired_angle_r = 0; //This is the angle in which we whant the
float desired_angle_y = 0;                         //balance to stay steady
float desired_angle_p = 0;

void setup() {
  Wire.begin(); //begin the wire comunication
  Wire.beginTransmission(0x68);
  Wire.write(0x6B);
  Wire.write(0);
  Wire.endTransmission(true);
  Serial.begin(115200);
  right_front_prop.attach(9); //attatch the right motor to pin 3
  left_front_prop.attach(3);  //attatch the left motor to pin 5
  right_back_prop.attach(11); //attatch the right motor to pin 3
  left_back_prop.attach(10);  //attatch the left motor to pin 5

  time = millis(); //Start counting time in milliseconds
  /*In order to start up the ESCs we have to send a min value
   * of PWM to them before connecting the battery. Otherwise
   * the ESCs won't start up or enter in the configure mode.
   * The min value is 1000us and max is 2000us, REMEMBER!*/
  left_back_prop.writeMicroseconds(1000); 
  right_back_prop.writeMicroseconds(1000);
  left_front_prop.writeMicroseconds(1000); 
  right_front_prop.writeMicroseconds(1000);
  
  MPUSetup();
  delay(2000); /*Give some delay, 7s, to have time to connect
                *the propellers and let everything start up*/ 
}//end of setup void

void loop() {

/////////////////////////////I M U/////////////////////////////////////
    timePrev = time;  // the previous time is stored before the actual time read
    time = millis();  // actual time read
    elapsedTime = (time - timePrev) / 1000; 
  
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
    //Serial.println(Total_angle[1]);
    MPULoop();

    
   
  
/*///////////////////////////P I D///////////////////////////////////*/
/*Remember that for the balance we will use just one axis. I've choose the x angle
to implement the PID with. That means that the x axis of the IMU has to be paralel to
the balance*/
//kp_r=analogRead(1);
//kp_r = map(kp_r, 0, 627, 0, 400);
//kp_r /= 100.0f;

//ki_r=analogRead(2);
//ki_r = map(ki_r, 0, 627, 0, 100);
//ki_r /= 100.0f;

//kd_r=analogRead(3);
//kd_r = map(kd_r, 0, 627, 0, 400);
//kd_r /= 100.0f;

/*First calculate the error between the desired angle and 
*the real measured angle*/
error_r = getRoll() - desired_angle_r;


//Serial.print("ERROR: ");
//Serial.print(error_r);
//Serial.print("KP,KI,KD: ");
//Serial.print(kp_r);
//Serial.print(", ");
//Serial.print(ki_r);
//Serial.print(", ");
//Serial.println(kd_r);
    
/*Next the proportional value of the PID is just a proportional constant
*multiplied by the error*/

pid_p_r = kp_r*error_r;

/*The integral part should only act if we are close to the
desired position but we want to fine tune the error. That's
why I've made a if operation for an error between -2 and 2 degree.
To integrate we just sum the previous integral value with the
error multiplied by  the integral constant. This will integrate (increase)
the value each loop till we reach the 0 point*/
if(-3 <error_r <3)
{
  pid_i_r = pid_i_r+(ki_r*error_r);  
}

/*The last part is the derivate. The derivate acts upon the speed of the error.
As we know the speed is the amount of error that produced in a certain amount of
time divided by that time. For taht we will use a variable called previous_error.
We substract that value from the actual error and divide all by the elapsed time. 
Finnaly we multiply the result by the derivate constant*/

pid_d_r = kd_r*((error_r - previous_error_r)/elapsedTime);

/*The final PID values is the sum of each of this 3 parts*/
PID_r = pid_p_r + pid_i_r + pid_d_r;

/*We know taht the min value of PWM signal is 1000us and the max is 2000. So that
tells us that the PID value can/s oscilate more than -1000 and 1000 because when we
have a value of 2000us the maximum value taht we could sybstract is 1000 and when
we have a value of 1000us for the PWM sihnal, the maximum value that we could add is 1000
to reach the maximum 2000us*/
if(PID_r < -700)
{
  PID_r=-700;
}
if(PID_r > 700)
{
  PID_r=700;
}

previous_error_r = error_r; //Remember to store the previous error.

/*Finnaly we calculate the PWM width. We sum the desired throttle and the PID value*/
throttle = 1350;
//throttle = map(throttle, 0, 1023, 1300, 2000);

//Serial.print("Throttle: ");
//Serial.println(throttle);

pwmLF = throttle - PID_r;
pwmRF = throttle + PID_r;
pwmLB = throttle - PID_r;
pwmRB = throttle + PID_r;


/*Once again we map the PWM values to be sure that we won't pass the min
and max values. Yes, we've already maped the PID values. But for example, for 
throttle value of 1300, if we sum the max PID value we would have 2300us and
that will mess up the ESC.*/
//RF
if(pwmRF < 1300)
{
  pwmRF= 1300;
}
if(pwmRF > 2000)
{
  pwmRF=2000;
}
//LF
if(pwmLF < 1300)
{
  pwmLF= 1300;
}
if(pwmLF > 2000)
{
  pwmLF=2000;
}
//RB
if(pwmRB < 1300)
{
  pwmRB = 1300;
}
if(pwmRB > 2000)
{
  pwmRB =2000;
}
//LB
if(pwmLB < 1300)
{
  pwmLB= 1300;
}
if(pwmLB > 2000)
{
  pwmLB =2000;
}

Serial.print("LF, RF, LB, RB: ");
Serial.print(pwmLF);
Serial.print(pwmRF);
Serial.print(pwmLB);
Serial.println(pwmRB);

Serial.print("Error: ");
Serial.println(error_r);

/*Finnaly using the servo function we create the PWM pulses with the calculated
width for each pulse*/
left_front_prop.writeMicroseconds(pwmLF);
right_front_prop.writeMicroseconds(pwmRF);
left_back_prop.writeMicroseconds(pwmLF);
right_back_prop.writeMicroseconds(pwmRF);

}//end of loop void
