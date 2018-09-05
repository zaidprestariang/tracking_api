var base_url = "http://prestariang.akaunsaya.com:5000";
var url_multiple = base_url + "/vehicleLocationHistory?deviceid=";
var url_websocket = base_url + "/gs-guide-websocket";
var url_devices = base_url + "/vehicleLocationHistoryLatest";
var stompClient = null;
$("#rightside").hide();
var markers = [];
var popups = [];
var timers = [];
var count = 0;
connect();
loadDevice();




function connect() {
    var socket = new SockJS(url_websocket);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {

        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/vehicleTracking', function (greeting) {

            var output = JSON.parse(greeting.body);

            if(output.deviceID == $('#deviceid').val() || $('#deviceid').val() == 'all'){
                showRealtimeMap(output);
            }

        });
    });
}
function loadDevice()
{
    var colorNames = [
        "#b40219",
        "#9db42d",
        "#2fb455",
        "#2452b4",
        "#9253b4",
        "#06b4a8",
        "#e5efb0",
        "#b66d42",
        "#6c690b",
        "#09570f",
        "#82ee05"
    ];

    $('#deviceid')
        .find('option')
        .remove()
        .end()
        .append('<option value="all">All device</option>')
        .val('all');

    $.getJSON( url_devices, function( data ) {
        markers = [];
        popups = [];
        var markerIndex = 0;

        $.each( data, function( key, val ) {
            console.log(val);
            ++markerIndex;
            var el = document.createElement('div');

            el.backgroundColor = "FF0000";
            el.color = colorNames[markerIndex];
            /*var marker = new mapboxgl.Marker(el, {
                offset: [0, -17.5]

            })*/



            addPopup(val.geometry.coordinates[1],val.geometry.coordinates[0],val.deviceID);

            var marker = new mapboxgl.Marker({ "color": colorNames[markerIndex]})
                .setLngLat([val.geometry.coordinates[1], val.geometry.coordinates[0]])
                .addTo(map);


            marker.color = colorNames[markerIndex];
            marker.name = val.deviceID;
            markers.push(marker);

            $('#deviceid').append( new Option(val.deviceID,val.deviceID,false,false) );
        });

        var deviceIDURL = getQueryParam('deviceid');

        //for lutfi iframe hack
        if(deviceIDURL != 'deviceid')
        {
            $('#deviceid').val(deviceIDURL).change();
        }

    });


}

function addPopup(long,lat,deviceid)
{
    var popup = new mapboxgl.Popup({closeOnClick: false, offset : 40})
        .setLngLat([long, lat])
        .setHTML(deviceid)
        .addTo(map);
    popups.push(popup);
}

function showRealtimeMap(message) {

    console.log("function showRealtimeMap(message) {");
    console.log(message);

    var Lat = message.geometry.coordinates[0];
    var Long = message.geometry.coordinates[1];

    console.log(Lat,Long);
    /*    marker.setLngLat([
            parseFloat(Long),
            parseFloat(Lat)
        ]);

        map.flyTo({center: [
                parseFloat(Long),
                parseFloat(Lat)
            ]});*/

    //requestAnimationFrame(showGreeting);
    popups.forEach(x => {
        x.remove();
});
    markers.forEach( (x,y) => {
        $(x.getElement()).hide();

    console.log("Hiding " + x.name);

    if(x.name == message.deviceID){

        console.log("Showing " + x.name);
        $(x.getElement()).show();

        markers[y].setLngLat([
            parseFloat(Long),
            parseFloat(Lat)
        ]);
        addPopup(x._lngLat.lng,x._lngLat.lat,x.name);
        map.flyTo({center: [
                x._lngLat.lng,
                x._lngLat.lat
            ]});
    }
});


}

mapboxgl.accessToken = 'pk.eyJ1IjoiaGFzc2FucHJlc3RhcmlhbmciLCJhIjoiY2psMXA4aWFiMWkxNDNwcXRidXJ4NGpwNCJ9.ZILPPxFgkleK1ica54v4fA';
var map = new mapboxgl.Map({
    container: 'map', // container id
    //style: 'mapbox://styles/mapbox/basic-v9',
    style: 'mapbox://styles/mapbox/dark-v9',
    center: [101.6519444, 2.9094875], // starting position
    zoom: 15 // starting zoom
});

// Add zoom and rotation controls to the map.
map.addControl(new mapboxgl.NavigationControl());
// Add geolocate control to the map.
map.addControl(new mapboxgl.GeolocateControl({
    positionOptions: {
        enableHighAccuracy: true
    },
    trackUserLocation: true
}));
//var marker = new mapboxgl.Marker();

/*function animateMarker(timestamp) {
    var radius = 20;

    // Update the data to a new position based on the animation timestamp. The
    // divisor in the expression `timestamp / 1000` controls the animation speed.
    marker.setLngLat([
        101.6519444,
        2.9094875
    ]);

    // Ensure it's added to the map. This is safe to call if it's already added.
    marker.addTo(map);

    // Request the next frame of the animation.
    //requestAnimationFrame(animateMarker);
}*/

var timer;
var berhenti = false;

$("#stop").click(function(){
    console.log(timer);
    berhenti = true;
    clearTimeout(timer);
    /* if (timer) {

         timer = 0;
     }*/
});
// Start the animation.
//requestAnimationFrame(animateMarker);
function replay(terminator = false,item,index,countParam,marker,total_count)
{
    //console.log("replay" + terminator);

    count++;

    marker.setLngLat([
        item.geometry.y,
        item.geometry.x
    ]);
    //console.log(item);
    popups.forEach(x => {
        //console.log("popup " + x);
        x.remove();
});
    popups = [];
    addPopup(item.geometry.y,item.geometry.x,item.deviceID);
    map.flyTo({center: [
            item.geometry.y,
            item.geometry.x
        ]});

    var timeToShow = moment(item.properties.timestamp).format("D/M/Y hh:mm:ss");

    $("#time").text(timeToShow);
    $("#countdown").text(count + '/' + total_count);




    // https://stackoverflow.com/questions/37977602/settimeout-not-working-inside-foreach
}

function dropdownChange()
{
    berhenti = false;
    var mode = $("#mode").val();
    console.log(mode)
    clearTimeout(timer);
    markers.forEach(x => {
        console.log("Hiding " + x.name);
    $(x.getElement()).hide();
});

    popups.forEach(x => {
        console.log("popup " + x);
    x.remove();
});

    if(mode == 'replay') {

        if($('#deviceid').val() == 'all'){

        } else {

            $("#rightside").show();

            $.getJSON( url_multiple + $('#deviceid').val(), function( data ) {
                var finalData = [];

                $.each( data, function( key, val ) {
                    finalData.push(val);
                });

                count = 0;
                var total_count = finalData.length;

                marker = accessMarkers($('#deviceid').val());

                $(marker.getElement()).show();

                addPopup(marker._lngLat.lng,marker._lngLat.lat,marker.name);
                map.flyTo({center: [
                        marker._lngLat.lng,
                        marker._lngLat.lat
                    ]});
                console.log(marker);

                /*for(i = 0; i < finalData.length ; i++)
                {
                    //console.log(finalData[i])
                    //replay(false, item, index, count,marker,total_count);
                    /!*timer = setTimeout(function() {

                    },200);*!/
                    //if(stopTimer > 1) retu
                    setTimeout(function() {
                        replay(false, finalData[i], i, count, marker, total_count);
                    },1000);
                }*/

                (function theLoop (i,counter,marker) {
                    console.log(i);
                    console.log(counter);
                    timer = setTimeout(function () {
                        //alert("Cheese!");
                        replay(false, finalData[counter], i, count, marker, total_count);
                        if (--i) {          // If i > 0, keep going
                            //replay2(false, finalData[i], i, count, marker, total_count);
                            theLoop(i,counter,marker);
                        }
                    }, 1500);
                    counter++;

                })(finalData.length,0,marker);


                /*finalData.forEach((item,index) => {
                    console.log(index)
                });*/

            });
        }
    } else if(mode == "realtime") {

        $("#rightside").hide();
        connect();

        if($('#deviceid').val() == 'all') {

            //loadDevice();

            markers.forEach(x => {
                $(x.getElement()).show();
            addPopup(x._lngLat.lng,x._lngLat.lat,x.name);
        })

        } else {


            markers.forEach(x => {
                console.log("Hiding " + x.name);
            $(x.getElement()).hide();

            if(x.name == $('#deviceid').val()){
                console.log("Showing " + x.name);
                $(x.getElement()).show();
                addPopup(x._lngLat.lng,x._lngLat.lat,x.name);
                map.flyTo({center: [
                        x._lngLat.lng,
                        x._lngLat.lat
                    ]});

            } else {

            }
        })
        }

    } else { console.log("Something is wrong") }
}
$("#mode").change(function(){
    console.log(markers);
    dropdownChange();
});

$("#deviceid").change(function(){
    console.log(markers);
    dropdownChange();
});

function accessMarkers(deviceid)
{
    for(i=0;markers.length;i++){
        if(markers[i].name == deviceid) {
            return markers[i];
        }
    }
}

$("#mapbox_style").change(function(){

    var layerId = $('#mapbox_style').val();
    map.setStyle('mapbox://styles/mapbox/' + layerId + '-v9');

});

function getQueryParam(param) {
    location.search.substr(1)
        .split("&")
        .some(function(item) { // returns first occurence and stops
            return item.split("=")[0] == param && (param = item.split("=")[1])
        })
    return param
}