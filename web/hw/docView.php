<?php
if (empty ($_FILES["file"]) || $_FILES["file"]["error"] > 0) {
  echo "<form action=\"docView.php\" method=\"POST\" enctype=\"multipart/form-data\"><label for=\"file\">Filename:</label><input type=\"file\" name=\"file\" id=\"file\"><br><input type=\"submit\" name=\"submit\" value=\"Submit\"></form>";
} else {
  $xml = simplexml_load_file ($_FILES["file"]["tmp_name"]);
  $media = $xml->component->nonXMLBody->text['mediaType'];
  $encoded = $xml->component->nonXMLBody->text;
  $decoded = base64_decode($encoded);
  $stamp = date ("U");
  $handle = fopen ("/var/www/$stamp" . "." . ($media == "application/pdf" ? "pdf" : "txt"), "w");
  fwrite ($handle, $decoded);
  fclose ($handle);
  echo "<a href=\"$stamp" . "." . ($media == "application/pdf" ? "pdf" : "txt") . "\">Click here to view file</a>";
}
?>
