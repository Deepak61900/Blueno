#include <Wire.h>
#include <HMC5883L.h>
#include <Timer.h>
#define hit A4
HMC5883L compass;
int errorMag = 0;
float angle = 0;


int motora_1 = 31, motora_2 = 30, motora_pwm = 9, pwm1Val = 0;
int motorb_1 = 24, motorb_2 = 25, motorb_pwm = 6, pwm2Val = 0;
int motorc_1 = 27, motorc_2 = 26, motorc_pwm = 7, pwm3Val = 0;
int motord_1 = 29, motord_2 = 28, motord_pwm = 8, pwm4Val = 0;

float kp = 1.2, kd = 2, ki = 0.2;
float magVal = 0,Az = 0;
float magDiff = 0,magErrL = 0,magInt = 0;

void setup()
{
  magInit();
  pinMode(motora_1,OUTPUT);
  pinMode(motora_2,OUTPUT);
  pinMode(motorb_1,OUTPUT);
  pinMode(motorb_2,OUTPUT);
  pinMode(motorc_1,OUTPUT);
  pinMode(motorc_2,OUTPUT);
  pinMode(motord_1,OUTPUT);
  pinMode(motord_2,OUTPUT);
  pinMode(motord_pwm,OUTPUT);
  pinMode(motora_pwm,OUTPUT);
  pinMode(motorb_pwm,OUTPUT);
  pinMode(motorc_pwm,OUTPUT);
  pinMode(26,OUTPUT);
  pinMode(27,INPUT);
  pinMode(28,OUTPUT);
  pinMode(29,OUTPUT);
  pinMode(hit,OUTPUT);
  Serial1.begin(38400);
  Serial.begin(38400);
  magVal = mag();
  magVal = mag();
  magVal = mag();
  magVal = mag();
  magVal += mag();
  magVal += mag();
  Az = magVal/3;
  while(Serial1.available())
  Serial1.read();
}
void loop()
{
  remoteread();
}

