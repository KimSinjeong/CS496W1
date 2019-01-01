const int rdir = 2;
const int rpwm = 3;
const int ren = 4;
const int ldir = 5;
const int lpwm = 6;
const int len = 7;

int rspd, lspd, divider = 8;
int rtmp, ltmp;

byte buf[1024];
int bufPos;

void setup() {
  Serial.begin(9600);
  while(!Serial) {
    ;
  }
  bufPos = 0;
  
  pinMode(rdir, OUTPUT);
  pinMode(rpwm, OUTPUT);
  pinMode(ren, OUTPUT);
  pinMode(ldir, OUTPUT);
  pinMode(lpwm, OUTPUT);
  pinMode(len, OUTPUT);
}

void loop() {
  if(Serial.available() > 0) {
    byte data = Serial.read();
    lspd = (int)(data & 0xf0) >> 4;
    rspd = (int)((data & 0x0f) << 4) >> 4;
    lspd = lspd < 8 ? lspd : lspd - 16;
    rspd = rspd < 8 ? rspd : rspd - 16;
    
    Serial.println(lspd);
    Serial.println(rspd);
    Serial.println("");
    
  }

  if(lspd < 0) { digitalWrite(len, LOW); digitalWrite(ldir, HIGH); ltmp = -lspd; divider = 8;}
  else if(lspd == 0) digitalWrite(len, HIGH);
  else { digitalWrite(len, LOW); digitalWrite(ldir, LOW); ltmp = lspd; divider = 7;}

  if(rspd < 0) { digitalWrite(ren, LOW); digitalWrite(rdir, LOW); rtmp = -rspd; divider = 8; }
  else if(rspd == 0) digitalWrite(ren, HIGH);
  else { digitalWrite(ren, LOW); digitalWrite(rdir, HIGH); rtmp = rspd; divider = 7; }

  analogWrite(rpwm, 255 * rtmp / divider);
  analogWrite(lpwm, 255 * ltmp / divider);

  delay(10);
}
