float accelAngle;
float gyroAngle;
float totalAngle;

float pid, pidP, pidI, pidD;
float error = 0;
float prevError = 0;

//double kp = 3;//c0.25;//3.44;
//double ki = 0;//0.0048;
//double kd = 1.5;//1.5;//1.92;//1.92;

int capValue = 128;

float getPID(float elapsedTime, float curAngle, float targetAngle)
{
  if(pid == 0){
    pidI = 0;
  }
  //Serial.println(curAngle);
  error = curAngle - targetAngle;
  //Serial.println(error);
  pidP = kp * error;
  if(-5 < error < 5){
    pidI = pidI + (ki * error);
  }
  pidD = kd *((error-prevError)/elapsedTime);
  
  pid = pidP + pidI + pidD;
  if(pid < -capValue){
    pid = -capValue;
  }
  if(pid > capValue){
    pid = capValue;
  }
  prevError = error;
  return pid;
}


