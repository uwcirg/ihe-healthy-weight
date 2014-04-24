$(document).ready(function() {
  
  function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
            var pair = vars[i].split("=");
            if(pair[0] == variable){return pair[1];}
    }
    return(false);
  }
  var ocNames = {
      11: "Management Occupations", 
      13: "Business and Financial Operations Occupations",
      15: "Computer and Mathematical Occupations",
      17: "Architecture and Engineering Occupations",
      19: "Life, Physical, and Social Science Occupations",
      23: "Legal Occupations",
      25: "Education, Training, and Library Occupations",
      27: "Arts, Design, Entertainment, Sports, and Media Occupations",
      29: "Healthcare Practitioners and Technical Occupations",
      31: "Healthcare Support Occupations",
      33: "Protective Service Occupations",
      35: "Food Preparation and Serving Related Occupations",
      37: "Building and Grounds Cleaning and Maintenance Occupations",
      39: "Personal Care and Service Occupations",
      41: "Sales and Related Occupations",
      43: "Office and Administrative Support Occupations",
      45: "Farming, Fishing, and Forestry Occupations",
      47: "Construction and Extraction Occupations",
      49: "Installation, Maintenance, and Repair Occupations",
      51: "Production Occupations",
      53: "Transportation and Material Moving Occupations"
  };
  var selectedOc = getQueryVariable('occupation');
  var hasOc = (selectedOc) ? true : false;
  var passedOccupation = false; 
  // Change select value for occupation to match URL string
  if (hasOc) {
      $("#occupationSelect").val(selectedOc);
      passedOccupation = ocNames[selectedOc];
  } 
  
  $("#occupationSelect").on('change', function(){
    var chosen = $(this).val();
    if (chosen) {
        var currentPage = window.location.href.substr(0, window.location.href.indexOf('?')); 
        window.location.href = currentPage + "?occupation=" + chosen;
    }
  });
  
  var colorArray = ['#AA4643', '#4572A7', '#89A54E', '#80699B', '#3D96AE', '#DB843D', '#92A8CD', '#A47D7C', '#B5CA92'];
  var color3 = ['#cc0000','#cc6600','#009900'];
  var color3more = ['#cc0000','#cc6600','#009900','#cc5252','#CC8F52','#3D993D'];
  var colorPinkBlue = ['#C4289E','#F733C7','#F87EDA','#3637C4','#4445F7','#8F90F8']
  Highcharts.setOptions({
    credits: {
      enabled: false
    }
  });  
    
  var options1 = {
    chart: {
      renderTo: 'container1',
      type: 'column',
      plotBackgroundColor: '#fffdf6'
    },
    legend: {
        reversed: true
    },
    colors: color3,
    title: {
      text: 'Dashboard 1 - BMI category by Age Group'
    },
    xAxis: {
        categories: [
            'Under 20','20-40','40-60','60+'
        ]
    },
    yAxis: {
      title: {
        text: 'Percentage'
      }
    },
    series: [],
    plotOptions: {
        column: {
            stacking: 'percent'
//            dataLabels: {
//                enabled: true,
//                color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
//                style: {
//                    textShadow: '0 0 3px black, 0 0 3px black'
//                }
//            }
        }
    },
    tooltip: {
        pointFormat: '<span style="color:{series.color}">{series.name}</span>: {point.percentage:.0f}%<br/>',
        shared: true
    }
  };    

  var options2 = {
    chart: {
      renderTo: 'container2',
      type: 'scatter',
      zoomType: 'xy',
      plotBackgroundColor: '#fffdf6'
    },
    colors: colorArray,
    title: {
      text: 'Dashboard 2 - BMI vs Age'
    },
    xAxis: {
        title: {
            text: 'Age'
        }
    },
    yAxis: {
      title: {
        text: 'BMI'
      }
    },
    series: [{
        name: 'Female',
        color: '#ff99ff',
        data: []
    },{
        name: 'Male',
        color: '#3399ff',
        data: []
    }],
    tooltip: {
        headerFormat: '<b>{series.name}</b><br>',
        pointFormat: 'Age: {point.x} - BMI: {point.y}'
    }
  };  
  
  var options3 = {
    chart: {
      renderTo: 'container3',
      type: 'column',
      plotBackgroundColor: '#fffdf6'
    },
    legend: {
        width: 300
        //reversed: true
    },
    colors: colorPinkBlue,
    title: {
      text: 'Dashboard 3 - BMI category by Age Group and Sex'
    },
    xAxis: {
        categories: [
            'Under 20','20-40','40-60','60+'
        ]
    },
    yAxis: {
      title: {
        text: 'Percentage'
      }
    },
    series: [],
    plotOptions: {
        column: {
            stacking: 'percent'
//            dataLabels: {
//                enabled: true,
//                color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
//                style: {
//                    textShadow: '0 0 3px black, 0 0 3px black'
//                }
//            }
        }
    },
    tooltip: {
        pointFormat: '<span style="color:{series.color}">{series.name}</span>: {point.percentage:.0f}%<br/>'
    }
  };      

  var options4 = {
    chart: {
      renderTo: 'container4',
      type: 'column'
    },
    legend: {
        reversed: true
    },
    colors: color3,
    title: {
      text: 'Dashboard 4 - BMI for '+passedOccupation+' vs Overall'
    },
    xAxis: {
        categories: [
            passedOccupation,'All'
        ]
    },
    yAxis: {
      title: {
        text: 'Percentage'
      }
    },
    series: [],
    plotOptions: {
        column: {
            stacking: 'percent'
        }
    },
    tooltip: {
        pointFormat: '<span style="color:{series.color}">{series.name}</span>: {point.percentage:.0f}%<br/>',
        shared: true
    }
  };    

  var options5 = {
    chart: {
      renderTo: 'container5',
      type: 'area'
    },
    colors: color3,
    title: {
      text: 'Dashboard 5 - BMI Percentage Over Time'
    },
    xAxis: {
        categories: ['2009','2010','2011','2012','2013','2014']
    },
    yAxis: {
      title: {
        text: 'Percentage'
      }
    },
    tooltip: {
        pointFormat: '<span style="color:{series.color}">{series.name}</span>: {point.percentage:.1f}%<br/>',
        shared: true
    },
    plotOptions: {
        area: {
            stacking: 'percent',
            lineColor: '#ffffff',
            lineWidth: 1,
            marker: {
                lineWidth: 1,
                lineColor: '#ffffff'
            }
        }
    },
    series: []
  };  

  var options6 = {
    chart: {
      renderTo: 'container6',
      type: 'line'
    },
    colors: colorArray,
    title: {
      text: 'Dashboard 6 - BMI Average Over Time'
    },
    tooltip: {
      valueDecimals: 2,
      shared: true,
      crosshairs: true
    },
    xAxis: {
        categories: ['2009','2010','2011','2012','2013','2014']
    },
    series: []
  };
  
  var options7 = {
    chart: {
      renderTo: 'container7',
      type: 'column'
    },
    legend: {
        reversed: true
    },
    colors: color3,
    title: {
      text: 'Dashboard 7 - BMI by Occupation Group'
    },
    xAxis: {
        categories: [
            
        ]
    },
    yAxis: {
      title: {
        text: 'Percentage'
      }
    },
    series: [{
                name: 'Obese',
                data: []
            }, {
                name: 'Overweight',
                data: []
            }, {
                name: 'Normal',
                data: []
            }],
    plotOptions: {
        column: {
            stacking: 'percent'
//            dataLabels: {
//                enabled: true,
//                color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
//                style: {
//                    textShadow: '0 0 3px black, 0 0 3px black'
//                }
//            }
        }
    },
    tooltip: {
        pointFormat: '<span style="color:{series.color}">{series.name}</span>: {point.percentage:.0f}%<br/>',
        shared: true
    }
  }; 

  var options8 = {
    chart: {
      renderTo: 'container8',
      type: 'column'
    },
    legend: {
    },
    colors: colorArray,
    title: {
      text: 'Dashboard 3 - BMI category by Age Group and Sex'
    },
    xAxis: {
        categories: []
    },
    yAxis: {
      title: {
        text: 'Percent Obese'
      },
      labels: {
        formatter:function() {
            return this.value + '%';
        }
      }
    },
    series: [{
                name: 'F: Obese',
                data: [],
                stack: "Female"
            }, {
                name: 'M: Obese',
                data: [],
                stack: "Male"
            }],
    tooltip: {
        valueDecimals: 0,
        pointFormat: '<span style="color:{series.color}">{series.name}</span>: {point.y}%<br/>'
    }
  }; 
  
    // option1 calculations
    var age1n = 0,
        age1o = 0,
        age1b = 0,
        age2n = 0,
        age2o = 0,
        age2b = 0,
        age3n = 0,
        age3o = 0,
        age3b = 0,
        age4n = 0,
        age4o = 0,
        age4b = 0;
    // option3 calculations
    var age1nf = 0,
        age1of = 0,
        age1bf = 0,
        age2nf = 0,
        age2of = 0,
        age2bf = 0,
        age3nf = 0,
        age3of = 0,
        age3bf = 0,
        age4nf = 0,
        age4of = 0,
        age4bf = 0;
    var age1nm = 0,
        age1om = 0,
        age1bm = 0,
        age2nm = 0,
        age2om = 0,
        age2bm = 0,
        age3nm = 0,
        age3om = 0,
        age3bm = 0,
        age4nm = 0,
        age4om = 0,
        age4bm = 0;
    
      // option4 vars
      var ocSeln = 0,
        ocSelo = 0,
        ocSelb = 0,
        ocAlln = 0,
        ocAllo = 0,
        ocAllb = 0;
        
    // options5 vars
    var n08 = 0,
        o08 = 0,
        b08 = 0,
        v08 = 0,
        s08 = 0,
        s08c = 0,
        n09 = 0,
        o09 = 0,
        b09 = 0,
        v09 = 0,
        s09 = 0,
        s09c = 0,
        n10 = 0,
        o10 = 0,
        b10 = 0,
        v10 = 0,
        s10 = 0,
        s10c = 0,
        n11 = 0,
        o11 = 0,
        b11 = 0,
        v11 = 0,
        s11 = 0,
        s11c = 0,
        n12 = 0,
        o12 = 0,
        b12 = 0,
        v12 = 0,
        s12 = 0,
        s12c = 0,
        n13 = 0,
        o13 = 0,
        b13 = 0,
        v13 = 0,
        s13 = 0,
        s13c = 0,
        n14 = 0,
        o14 = 0,
        b14 = 0,
        v14 = 0,
        s14 = 0,
        s14c = 0;
        
      var opt7Array = [];
      var opt8Array = [];

      function buildPercents(toAdd,theArray,bmi,gender) {
        var checkIt = $.inArray(toAdd, theArray);
        if (checkIt == -1) {
            theArray.push(toAdd);
            theArray[toAdd] = [];
            theArray[toAdd].b = 0;
            theArray[toAdd].o = 0;
            theArray[toAdd].n = 0;
            if (gender) {
                theArray[toAdd].bm = 0;
                theArray[toAdd].om = 0;
                theArray[toAdd].nm = 0;
            }
        } 
        if (bmi >= 30) {
            if(gender=='M') {
                theArray[toAdd].bm++;
            } else {
                theArray[toAdd].b++;
            }
        } else if (bmi >= 25) {
            if(gender=='M') {
                theArray[toAdd].om++;
            } else {
                theArray[toAdd].o++;
            }
        } else {
            if(gender=='M') {
                theArray[toAdd].nm++;
            } else {
                theArray[toAdd].n++;
            }
        }  
      }
        
      function SortByName(a, b){
        var aName = a.toLowerCase();
        var bName = b.toLowerCase(); 
        return ((aName < bName) ? -1 : ((aName > bName) ? 1 : 0));
      }  
      
  $.getJSON('../data/visits_Feb_22.json', function(data) {

    $.each(data, function(key, val) {
      
      // option1 calcs
      if (val.age > 60) {
          if (val.bmi >= 30 ) {
              age4b++
              if (val.gender == 'F') { age4bf++ } else { age4bm++ }
          } else if (val.bmi >= 25 ) {
              age4o++
              if (val.gender == 'F') { age4of++ } else { age4om++ }
          } else {
              age4n++
              if (val.gender == 'F') { age4nf++ } else { age4nm++ }
          }
      } else if (val.age > 40) {
          if (val.bmi >= 30 ) {
              age3b++
              if (val.gender == 'F') { age3bf++ } else { age3bm++ }
          } else if (val.bmi >= 25 ) {
              age3o++
              if (val.gender == 'F') { age3of++ } else { age3om++ }
          } else {
              age3n++
              if (val.gender == 'F') { age3nf++ } else { age3nm++ }
          }       
      } else if (val.age > 20) {
          if (val.bmi >= 30 ) {
              age2b++
              if (val.gender == 'F') { age2bf++ } else { age2bm++ }
          } else if (val.bmi >= 25 ) {
              age2o++
              if (val.gender == 'F') { age2of++ } else { age2om++ }
          } else {
              age2n++
              if (val.gender == 'F') { age2nf++ } else { age2nm++ }
          }
      } else {
          if (val.bmi >= 30 ) {
              age1b++
              if (val.gender == 'F') { age1bf++ } else { age1bm++ }
          } else if (val.bmi >= 25 ) {
              age1o++
              if (val.gender == 'F') { age1of++ } else { age1om++ }
          } else {
              age1n++
              if (val.gender == 'F') { age1nf++ } else { age1nm++ }
          }     
      }
      
      // option2 calcs
//      if (val.gender == 'F') {
//        options2.series[0].data.push([val.age,val.bmi]);
//      } else {
//        options2.series[1].data.push([val.age,val.bmi]);
//      }
      
      // option4 calc
      if (hasOc) {
          if (val.bmi >= 30 ) {
            if (val.o23 == selectedOc) { ocSelb++ }
            ocAllb++;
          } else if (val.bmi >= 25 ) {
            if (val.o23 == selectedOc) { ocSelo++ }
            ocAllo++;
          } else {
            if (val.o23 == selectedOc) { ocSeln++ }
            ocAlln++;
          }          
      }
      
      // option5 calc
      var obsDate = new Date(val.date);
      var obsYear = obsDate.getFullYear();
      if (obsYear == 2009) {
        v09 += val.bmi;
        if (val.o23 == selectedOc) { s09 += val.bmi; s09c++; }
        if (val.bmi >= 30 ) {
          b09++;
        } else if (val.bmi >= 25 ) {
          o09++;
        } else {
          n09++;
        }
      } else if (obsYear == 2010) {
        v10 += val.bmi;
        if (val.o23 == selectedOc) { s10 += val.bmi; s10c++; }
        if (val.bmi >= 30 ) {
          b10++;
        } else if (val.bmi >= 25 ) {
          o10++;
        } else {
          n10++;
        }
      } else if (obsYear == 2011) {
        v11 += val.bmi;
        if (val.o23 == selectedOc) { s11 += val.bmi; s11c++; }
        if (val.bmi >= 30 ) {
          b11++;
        } else if (val.bmi >= 25 ) {
          o11++;
        } else {
          n11++;
        }
      } else if (obsYear == 2012) {
        v12 += val.bmi;
        if (val.o23 == selectedOc) { s12 += val.bmi; s12c++; }
        if (val.bmi >= 30 ) {
          b12++;
        } else if (val.bmi >= 25 ) {
          o12++;
        } else {
          n12++;
        }
      } else if (obsYear == 2013) {
        v13 += val.bmi;
        if (val.o23 == selectedOc) { s13 += val.bmi; s13c++; }
        if (val.bmi >= 30 ) {
          b13++;
        } else if (val.bmi >= 25 ) {
          o13++;
        } else {
          n13++;
        }
      } else {
        v14 += val.bmi;
        if (val.o23 == selectedOc) { s14 += val.bmi; s14c++; }
        if (val.bmi >= 30 ) {
          b14++;
        } else if (val.bmi >= 25 ) {
          o14++;
        } else {
          n14++;
        }  
      }
      
      buildPercents(val.o8,opt7Array,val.bmi);
      
      buildPercents(val.o8,opt8Array,val.bmi,val.gender);
      
//      var obsDate2 = new Date(val.date);
//      var obsYear2 = obsDate2.getFullYear();
//      if (obsYear2 == 2013) {
//        buildPercents(obsYear2,opt8Array,val.bmi);
//      }
      
      
//        // options7
//        var foo = val.o8;
//        var checkIt = $.inArray(foo, goat);
//        if (checkIt == -1) {
//            goat.push(foo);
//            goat[foo] = [];
//            goat[foo].b = 0;
//            goat[foo].o = 0;
//            goat[foo].n = 0;
//        } 
//        if (val.bmi >= 30) {
//            goat[foo].b++;
//        } else if (val.bmi >= 25) {
//            goat[foo].o++;
//        } else {
//            goat[foo].n++;
//        }
      
    }); //$.each(data, function(key, val) {

    //console.log(opt7Array);
    console.log(opt8Array);
    opt7Array.sort(SortByName);
    $.each(opt7Array, function(key1, val1) {
        options7.xAxis.categories.push(val1);
        var totalCount = opt7Array[val1]['n'] + opt7Array[val1]['o'] + opt7Array[val1]['b'];
        options7.series[2].data.push(opt7Array[val1]['n']/totalCount);
        options7.series[1].data.push(opt7Array[val1]['o']/totalCount);
        options7.series[0].data.push(opt7Array[val1]['b']/totalCount);
    });
    $.each(opt8Array, function(key1, val1) {
        options8.xAxis.categories.push(val1);
        var totalCountF = opt8Array[val1]['n'] + opt8Array[val1]['o'] + opt8Array[val1]['b'];
        var totalCountM = opt8Array[val1]['nm'] + opt8Array[val1]['om'] + opt8Array[val1]['bm'];
        options8.series[1].data.push(opt8Array[val1]['bm']/totalCountM*100);
        options8.series[0].data.push(opt8Array[val1]['b']/totalCountF*100);
    });
    
    // Tally option1
    var age1 = age1n + age1o + age1b;
    var age2 = age2n + age2o + age2b;
    var age3 = age3n + age3o + age3b;
    var age4 = age4n + age4o + age4b;    
    options1.series.push({
      name: "Obese",
      data: [(age1b/age1),(age2b/age2),(age3b/age3),(age4b/age4)]
    },{
      name: "Overweight",
      data: [(age1o/age1),(age2o/age2),(age3o/age3),(age4o/age4)]
    },{
      name: "Normal",
      data: [(age1n/age1),(age2n/age2),(age3n/age3),(age4n/age4)]
    });
    
    // Tally option3
    var age1f = age1nf + age1of + age1bf;
    var age2f = age2nf + age2of + age2bf;
    var age3f = age3nf + age3of + age3bf;
    var age4f = age4nf + age4of + age4bf;    
    var age1m = age1nm + age1om + age1bm;
    var age2m = age2nm + age2om + age2bm;
    var age3m = age3nm + age3om + age3bm;
    var age4m = age4nm + age4om + age4bm;    
    options3.series.push({
      name: "F: Obese",
      data: [(age1bf/age1f),(age2bf/age2f),(age3bf/age3f),(age4bf/age4f)],
      stack: "Female"
    },{
      name: "F: Overweight",
      data: [(age1of/age1f),(age2of/age2f),(age3of/age3f),(age4of/age4f)],
      stack: "Female"
    },{
      name: "F: Normal",
      data: [(age1nf/age1f),(age2nf/age2f),(age3nf/age3f),(age4nf/age4f)],
      stack: "Female"
    },{
      name: "M: Obese",
      data: [(age1bm/age1m),(age2bm/age2m),(age3bm/age3m),(age4bm/age4m)],
      stack: "Male"
    },{
      name: "M: Overweight",
      data: [(age1om/age1m),(age2om/age2m),(age3om/age3m),(age4om/age4m)],
      stack: "Male"
    },{
      name: "M: Normal",
      data: [(age1nm/age1m),(age2nm/age2m),(age3nm/age3m),(age4nm/age4m)],
      stack: "Male"
    });

    // Tally option4
    var ocSel = ocSeln + ocSelo + ocSelb;
    var ocAll = ocAlln + ocAllo + ocAllb;
    options4.series.push({
      name: "Obese",
      data: [(ocSelb/ocSel),(ocAllb/ocAll)]
    },{
      name: "Overweight",
      data: [(ocSelo/ocSel),(ocAllo/ocAll)]
    },{
      name: "Normal",
      data: [(ocSeln/ocSel),(ocAlln/ocAll)]
    });
    // Tally option5
    var a08 = b08 + o08 + n08;
    var a09 = b09 + o09 + n09;
    var a10 = b10 + o10 + n10;
    var a11 = b11 + o11 + n11;
    var a12 = b12 + o12 + n12;
    var a13 = b13 + o13 + n13;
    var a14 = b14 + o14 + n14;
    options5.series.push({
      name: "Obese",
      data: [(b09/a09),(b10/a10),(b11/a11),(b12/a12),(b13/a13),(b14/14)]
    },{
      name: "Overweight",
      data: [(o09/a09),(o10/a10),(o11/a11),(o12/a12),(o13/a13),(o14/14)]
    },{
      name: "Normal",
      data: [(n09/a09),(n10/a10),(n11/a11),(n12/a12),(n13/a13),(n14/14)]
    });
    
    options6.series.push({
      name: passedOccupation,
      data: [(s09/s09c),(s10/s10c),(s11/s11c),(s12/s12c),(s13/s13c),(s14/s14c)]
    },{
      name: "All",
      data: [(v09/a09),(v10/a10),(v11/a11),(v12/a12),(v13/a13),(v14/a14)]
    });
    
    // Generate charts
    //var chart1 = new Highcharts.Chart(options1);
    //var chart2 = new Highcharts.Chart(options2);
    var chart3 = new Highcharts.Chart(options3);
    var chart5 = new Highcharts.Chart(options5);
    if (hasOc) {
        var chart4 = new Highcharts.Chart(options4);
        var chart6 = new Highcharts.Chart(options6);
    }
    var chart7 = new Highcharts.Chart(options7);
    var chart8 = new Highcharts.Chart(options8);
    
  });
  
}); // $(document).ready(function() {