#define READ_PIN 0
#define MOTOR_1 3
#define MOTOR_2 9
#define MOTOR_3 10
#define MOTOR_4 11

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  
}

int input;

void loop() {
  // put your main code here, to run repeatedly:
  input = analogRead(READ_PIN);
  input = map(input, 0, 1023, 0, 255);
  Serial.println(input);
  analogWrite(MOTOR_1, input);
  analogWrite(MOTOR_2, input);
  analogWrite(MOTOR_3, input);
  analogWrite(MOTOR_4, input);
}
