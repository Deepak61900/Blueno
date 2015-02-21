void remoteread()
{
  Serial1.write('a');
  while(!Serial1.available())
  {
  }
  angle = Serial1.parseInt();
  if(angle>700)
  {
    //Serial.print("Stall");
    stall();
  }
  else
  {
    angle = ((angle)/100);
    magFeed(60,angle);
  }
  //Serial.println(angle);
}
