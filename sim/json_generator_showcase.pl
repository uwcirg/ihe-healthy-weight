#!/usr/bin/perl

use DBI;
use JSON;
use Data::Dumper;

# Setup
$dbh = DBI->connect ("dbi:mysql:ihe2014", "phr_rw", "", { RaiseError => 1, PrintError => 1, AutoCommit => 0 });
$sql = <<EOF;
SELECT
 ifnull(gender, 'U') as gender,
 ifnull(ethnicity, 'Other') as ethnicity,
 ifnull(zip_code, '98125') as zip_code,
 ifnull(calculated_bmi, '0') as calculated_bmi,
 ifnull(calculated_age, '0') as calculated_age,
 date_format(obs_date, '%Y-%m-%d %T') as obs_date,
 ifnull(occupation_8, 'None') as occupation_8,
 case when occupation_23 is null then 'None'
  else substring(occupation_23, 1, instr(occupation_23, '(') - 2)
  end as occupation_23,
 case when occupation_23 is null then '0'
  else substring(occupation_23, instr(occupation_23, '(') + 1, instr(occupation_23, ')') - instr(occupation_23, '(') - 1)
  end as occupation_23_code,
 case when physical_quantity is null or freq_physical is null then '0'
  when
   (
    (import_source like 'visits_%' and physical_quantity < 150)
    or
    (import_source like 'showcase%' and freq_physical * physical_quantity < 150)
   ) then '1'
  when
   (
    (import_source like 'visits_%' and physical_quantity between 150 and 299)
    or
    (import_source like 'showcase%' and freq_physical * physical_quantity between 150 and 299)
   ) then '2'
  when
   (
    (import_source like 'visits_%' and physical_quantity >= 300)
    or
    (import_source like 'showcase%' and freq_physical * physical_quantity >= 300)
   ) then '3'
  end as physical_quantity,
 import_source
FROM healthy_weight_obs
where import_source like 'showcaseData%' and obs_date <= date_add(now(), interval 3 hour)
EOF
$sth = $dbh->prepare($sql);
$rv = $sth->execute;

# Output
$out = "";
while ($row = $sth->fetchrow_hashref) {
#  $out .= encode_json($row);
  $out .= "{";
  foreach $key (sort(keys ($row))) {
    $out .= "\"$key\":";
    if ($key eq "zip_code" || $key eq "calculated_bmi" ||
        $key eq "calculated_age" || $key eq occupation_23_code ||
        $key eq "physical_quantity" || $key eq "freq_physical") {
      $out .= $$row{$key};
    } else {
      $out .= "\"" . $$row{$key} . "\"";
    }
    $out .=  "," unless ($key eq "zip_code");
  }
  $out .= "},";
}
print "[" . substr($out, 0, -1) . "]";

# Cleanup
$dbh->disconnect;
