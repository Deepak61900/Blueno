void stall()
{
  digitalWrite(motora_1,HIGH);
  digitalWrite(motora_2,HIGH);
  digitalWrite(motorb_1,HIGH);
  digitalWrite(motorb_2,HIGH);
  digitalWrite(motorc_1,HIGH);
  digitalWrite(motorc_2,HIGH);
  digitalWrite(motord_1,HIGH);
  digitalWrite(motord_2,HIGH);
}

void rotate_clock(float v)
{
  digitalWrite(motora_1,LOW);
  digitalWrite(motora_2,HIGH);
  analogWrite(motora_pwm,v);
 
  digitalWrite(motorb_1,HIGH);
  digitalWrite(motorb_2,LOW);
  analogWrite(motorb_pwm,v);
  
  digitalWrite(motorc_1,HIGH);
  digitalWrite(motorc_2,LOW);
  analogWrite(motorc_pwm,v);
  
  digitalWrite(motord_1,LOW);
  digitalWrite(motord_2,HIGH);
  analogWrite(motord_pwm,v);
}

void rotate_anti_clock(float v)
{
  digitalWrite(motora_1,HIGH);
  digitalWrite(motora_2,LOW);
  analogWrite(motora_pwm,v);
  
  digitalWrite(motorb_1,LOW);
  digitalWrite(motorb_2,HIGH);
  analogWrite(motorb_pwm,v);
  
  digitalWrite(motorc_1,LOW);
  digitalWrite(motorc_2,HIGH);
  analogWrite(motorc_pwm,v);
  
  digitalWrite(motord_1,HIGH);
  digitalWrite(motord_2,LOW);
  analogWrite(motord_pwm,v);
}

void magFeed(float vRef,float angle)
{ 
    pwm1Val = -(vRef)*flFilter(cos(PI/4 + angle));
    pwm2Val = (vRef)*flFilter(cos(PI/4 - angle));
    pwm3Val = -(vRef)*flFilter(cos(PI/4 + angle));
    pwm4Val = (vRef)*flFilter(cos(PI/4 - angle));

      magVal = mag() - Az;
      if(magVal>300)
      {
        magVal=-(360-magVal);
      }
      if(magVal<-300)
      {
        magVal=(360+magVal);
      }
      magDiff = magVal - magErrL;
      magErrL = magVal;
      magInt += magVal;
    
      pwm1Val += ((kp*magVal)+(kd*magDiff)+(ki*magInt));
      pwm2Val -= ((kp*magVal)+(kd*magDiff)+(ki*magInt));
      pwm3Val -= ((kp*magVal)+(kd*magDiff)+(ki*magInt));
      pwm4Val += ((kp*magVal)+(kd*magDiff)+(ki*magInt));
    
    if(pwm1Val > 0)
    {
      digitalWrite(motora_1,HIGH);
      digitalWrite(motora_2,LOW);
    }
    else
    if(pwm1Val <0)
    {
      digitalWrite(motora_1,LOW);
      digitalWrite(motora_2,HIGH);
    }
    else
    {
      digitalWrite(motora_1,HIGH);
      digitalWrite(motora_2,HIGH);
    }
    
    if(pwm2Val >0)
    {
      digitalWrite(motorb_1,HIGH);
      digitalWrite(motorb_2,LOW);
    }
    else
    if(pwm2Val <0)
    {
      digitalWrite(motorb_1,LOW);
      digitalWrite(motorb_2,HIGH);
    }
    else
    {
      digitalWrite(motorb_1,HIGH);
      digitalWrite(motorb_2,HIGH);
    }
    
    if(pwm3Val >0)
    {
      digitalWrite(motorc_1,HIGH);
      digitalWrite(motorc_2,LOW);
    }
    else
    if(pwm3Val <0)
    {
      digitalWrite(motorc_1,LOW);
      digitalWrite(motorc_2,HIGH);
    }
    else
    {
      digitalWrite(motorc_1,HIGH);
      digitalWrite(motorc_2,HIGH);
    }
    
    if(pwm4Val > 0)
    {
      digitalWrite(motord_1,HIGH);
      digitalWrite(motord_2,LOW);
    }
    else
    if(pwm4Val <0)
    {
      digitalWrite(motord_1,LOW);
      digitalWrite(motord_2,HIGH);
    }
    else
    {
      digitalWrite(motord_1,HIGH);
      digitalWrite(motord_2,HIGH);
    }
   
    
    pwm1Val = limit(mod(pwm1Val));
    pwm2Val = limit(mod(pwm2Val));
    pwm3Val = limit(mod(pwm3Val));
    pwm4Val = limit(mod(pwm4Val));
    
    
    analogWrite(motora_pwm,pwm1Val);
    analogWrite(motorb_pwm,pwm2Val);
    analogWrite(motorc_pwm,pwm3Val);
    analogWrite(motord_pwm,pwm4Val);
    
//      Serial.print(pwm1Val);
//      Serial.print("  ");
//      Serial.print(pwm2Val);
//      Serial.print("  ");
//      Serial.print(pwm3Val);
//      Serial.print("  ");
//      Serial.println(pwm4Val); 
//  }
}

float mod(float a)
{
  if(a < 0)
  {
    a = -1*a;
  }
  return a;
}

float limit(float a)
{
  if(a >= 100)
  a = 100;
  else
  if(a < 0)
  a = 0;
  return a;
}

int signOf(float a)
{
  int x;
  if(a < -0.0001)
  x = -1;
  else
  if(a > 0.0001)
  x = 1;
  else x = 0;
  return x;
}

float flFilter(float a)
{
  float x;
  if(a < -0.0001)
  x = a;
  else
  if(a > 0.0001)
  x = a;
  else x = 0;
  return x;
}

void magFilter(float val)
{
  if(val < 90)
  {
    val += 90;
  }
  else
  if(val > 270)
  {
    val -= 90;
  }
}
