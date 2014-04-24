#!/usr/bin/perl

#use lib "/home/jsibley/perl5/lib/perl5";
use Text::CSV;

$csv = Text::CSV->new();

if ($#ARGV == -1) {
  print "Need to give me the value to use for 'import_filename'.\n";
  exit;
}

$line = 1;

while (<STDIN>) {
  next if ($line++ == 1);
  s/'/''/g;
  if ($csv->parse($_)) {
    @fields = $csv->fields;
    print "insert into healthy_weight_obs (obs_date, zip_code, birthdate, calculated_age, gender, ethnicity, occupation_41, occupation_23, occupation_8, weight_pounds, height_inches, calculated_bmi, physical_quantity, freq_physical, patient_id, import_source, import_datetime) values ";
    print "('" . trim($fields[0]) . "', '" . trim($fields[1]) . "', '" . trim($fields[2]) . "', '" . trim($fields[3]) . "', '" . substr(trim($fields[4]), 0, 1) . "', '" . trim($fields[5]) . "', '" . trim($fields[6]) . "', '" . trim($fields[7]) . "', '" . trim($fields[8]) . "', '" . trim($fields[9]) . "', '" . trim($fields[10]) . "', '" . trim($fields[11]) . "',  '" . trim($fields[12]) . "', '" . trim($fields[13]) . "', round(rand() * 100000), '" . $ARGV[0] . "', now());\n";
  }
}

sub trim {
    my $string = shift;

    $string =~ s/^\s+//;
    $string =~ s/\s+$//;
    return $string;
}

