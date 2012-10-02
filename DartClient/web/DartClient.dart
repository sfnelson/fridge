
#import('dart:html');
#import('dart:crypto');
#import('dart:math');

void main() {
  query("#login").on.click.add(login);
}

void success(HttpRequest request) {
  document.window.alert(request.responseText);
}

void login(Event event) {
  String username = query("#username").value;
  String password = query("#password").value;
  String cnonce = generateNonce();
  int timestamp = (new Date.now().millisecondsSinceEpoch / 1000).toInt();
  
  List<int> secret = new MD5().update(password.charCodes()).digest();
  HMAC hmac = new HMAC(new MD5(), secret);
  
  hmac.update('$cnonce,$timestamp,$username'.charCodes());
  
  String signature = CryptoUtils.bytesToHex(hmac.digest());
  
  String base = 'http://localhost:8888/fridge/rest/request_nonce';
  String url = '$base?cnonce=$cnonce&timestamp=$timestamp&username=$username&hmac=$hmac';
  new HttpRequest.get(url, success);
}

Random r = new Random();
String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
String generateNonce() {
  String nonce = "";
  for (int i = 0; i < 20; i++) {
    nonce = nonce.concat(chars[r.nextInt(chars.length)]);
  }
}
