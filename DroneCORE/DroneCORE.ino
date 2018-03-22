#define READ_PIN 0
#define MOTOR_1 3
#define MOTOR_2 9
#define MOTOR_3 10
#define MOTOR_4 11


#define PROGRAMMING true
#define STABILIZING true

void setup() {
  // put your setup code here, to run once:
  
  Serial.begin(19200);
  initializeESCs();
  
  #ifdef PROGRAMMING
  programmerLoop();
  #endif
  
  #ifdef STABILIZING
  stabSetup();
  #endif
}

int input;


float biasESC1 = 1.0f;
float biasESC2 = 1.0f;
float biasESC3 = 1.0f;
float biasESC4 = 1.0f;

double kp = 1.0f;
double ki = 1.0f;
double kd = 1.0f;

void loop() {
  //#ifdef PROGRAMMING
  //programmerLoop();
  //#endif

  int tmp = analogRead(READ_PIN);
  tmp = map(tmp, 0, 614, 700, 1400);
  biasESC4 = 0.001f * (float)tmp;
  biasESC2 = 1.4f - (0.001f * (float)(tmp-700));    
  
  Serial.print("B2: ");
  Serial.print(biasESC2);
  Serial.print(", B4: ");
  Serial.println(biasESC4);

  Serial.print("(Kp, Ki, Kd): ");
  Serial.print(kp);
  Serial.print(", ");
  Serial.print(ki);
  Serial.print(", ");
  Serial.print(kd);
  Serial.println(")");

  #ifdef STABILIZING
  stabLoop();
  #endif
  // put your main code here, to run repeatedly:
  /*
  input = analogRead(READ_PIN);
  input = map(input, 0, 1023, 0, 255);
  Serial.println(input);
  analogWrite(MOTOR_1, input);
  analogWrite(MOTOR_2, input);
  analogWrite(MOTOR_3, input);
  analogWrite(MOTOR_4, input);
  */
}
