
int get_pid(float kp, float ki, float kd, float* pid_i, float error, float* previous_error)
{
  float PID, pid_p, pid_d;

  pid_p = kp * error;

  /*The integral part should only act if we are close to the
  desired position but we want to fine tune the error. That's
  why I've made a if operation for an error between -2 and 2 degree.
  To integrate we just sum the previous integral value with the
  error multiplied by  the integral constant. This will integrate (increase)
  the value each loop till we reach the 0 point*/
  if(-3 < error <3)
  {
    *pid_i = *pid_i + (ki * error);  
  }
  
  /*The last part is the derivate. The derivate acts upon the speed of the error.
  As we know the speed is the amount of error that produced in a certain amount of
  time divided by that time. For taht we will use a variable called previous_error.
  We substract that value from the actual error and divide all by the elapsed time. 
  Finnaly we multiply the result by the derivate constant*/
  
  pid_d = kd * ( (error - *previous_error) / elapsedTime);
  
  /*The final PID values is the sum of each of this 3 parts*/
  PID = pid_p + *pid_i + pid_d;
  
  /*We know taht the min value of PWM signal is 1000us and the max is 2000. So that
  tells us that the PID value can/s oscilate more than -1000 and 1000 because when we
  have a value of 2000us the maximum value taht we could sybstract is 1000 and when
  we have a value of 1000us for the PWM sihnal, the maximum value that we could add is 1000
  to reach the maximum 2000us*/
  if(PID < -700)
  {
    PID = -700;
  }
  if(PID > 700)
  {
    PID = 700;
  }
  
  *previous_error = error;
  
  return PID;
}

