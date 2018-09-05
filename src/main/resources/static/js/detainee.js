var base_url = "http://prestariang.akaunsaya.com:5000";
var url_multiple = base_url + "/detaineeLocationHistory?deviceid=";
var url_websocket = base_url + "/gs-guide-websocket";
var url_devices = base_url + "/detaineeLocationHistoryLatest";
var stompClient = null;


$("#rightside").hide();
var markers = [];
connect();
loadDevice();
function connect() {
    var socket = new SockJS(url_websocket);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {

        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/detaineeTracking', function (greeting) {

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
        var markerIndex = 0;

        $.each( data, function( key, val ) {
            //console.log(val);
            ++markerIndex;
            var el = document.createElement('div');

            el.backgroundColor = "FF0000";
            el.color = colorNames[markerIndex];
            /*var marker = new mapboxgl.Marker(el, {
                offset: [0, -17.5]

            })*/
            var marker = new mapboxgl.Marker({ "color": colorNames[markerIndex]})
                .setLngLat([val.geometry.coordinates[1], val.geometry.coordinates[0]])
                .addTo(map);

            marker.color = colorNames[markerIndex];
            marker.name = val.deviceID;
            markers.push(marker);

            $('#deviceid').append( new Option(val.deviceID,val.deviceID,false,false) );
        });

    });
}
function showRealtimeMap(message) {

    console.log("function showRealtimeMap(message) {");
    console.log(message);

    var Lat = message.geometry.coordinates[0];
    var Long = message.geometry.coordinates[1];

    console.log(Lat,Long);

    markers.forEach( (x,y) => {

        if(x.name == message.deviceID){

            markers[y].setLngLat([
                parseFloat(Long),
                parseFloat(Lat)
            ]);

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
    //style: mapStyle,
    style: 'mapbox://styles/mapbox/dark-v9',
    center: [101.6529900,2.911955], // starting position
    zoom: 23 // starting zoom
});

/*[101.6518507,2.911842],
[101.6528507,2.911842],
[101.6528507,2.911360],
[101.6518507,2.911360]*/
loadlayer();

function loadlayer()
{
    map.on('load', function() {
        map.addSource("myImageSource", {
            "type": "image",
            "url": "http://prestariang.akaunsaya.com:5000/img/floorplan.png",
            "coordinates": [
                [101.6528700,2.912030],
                [101.6531200,2.912030],
                [101.6531200,2.911700],
                [101.6528700,2.911700]
            ]
        });

        map.addLayer({
            "id": "overlay",
            "source": "myImageSource",
            "type": "raster",
            "paint": {
                "raster-opacity": 0.85
            }
        });
    });
}
function relayer()
{
    map.on('styledata', function() {

        console.log("meow");
        //map.removeSource("myImageSource");
        //map.removeLayer("overlay");
        //

        map.addSource("myImageSource", {
            "type": "image",
            "url": "http://prestariang.akaunsaya.com:5000/img/floorplan.png",
            "coordinates": [
                [101.6528700,2.912030],
                [101.6531200,2.912030],
                [101.6531200,2.911700],
                [101.6528700,2.911700]
            ]
        });

        map.addLayer({
            "id": "overlay",
            "source": "myImageSource",
            "type": "raster",
            "paint": {
                "raster-opacity": 0.85
            }
        });
    });
}




// Add zoom and rotation controls to the map.
map.addControl(new mapboxgl.NavigationControl());
// Add geolocate control to the map.
map.addControl(new mapboxgl.GeolocateControl({
    positionOptions: {
        enableHighAccuracy: true
    },
    trackUserLocation: true
}));
var marker = new mapboxgl.Marker();

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



// Start the animation.
//requestAnimationFrame(animateMarker);

function dropdownChange()
{
    var mode = $("#mode").val();
    console.log(mode)

    if(mode == 'replay') {

        if($('#deviceid').val() == 'all'){

        } else {

            $("#rightside").show();
            var count = 0;
            $.getJSON( url_multiple + $('#deviceid').val(), function( data ) {
                var finalData = [];

                $.each( data, function( key, val ) {
                    finalData.push(val);
                });

                var total_count = finalData.length;

                var marker = accessMarkers($('#deviceid').val());

                finalData.forEach((item,index) => {

                    setTimeout(function(){
                        count++;

                        console.log(item.geometry.y);
                        console.log(item.geometry.x );

                        marker.setLngLat([
                            item.geometry.y,
                            item.geometry.x
                        ]);

                        map.flyTo({center: [
                                item.geometry.y,
                                item.geometry.x
                            ]});

                        var timeToShow = moment(item.properties.timestamp).format("D/M/Y hh:mm:ss");

                        $("#time").text(timeToShow);
                        $("#countdown").text(count + '/' + total_count);

                    }, index * 1000);
                    // https://stackoverflow.com/questions/37977602/settimeout-not-working-inside-foreach
                });

            });
        }
    } else if(mode == "realtime") {

        $("#rightside").hide();
        connect();

        if($('#deviceid').val() == 'all') {

            loadDevice();

        } else {

            markers.forEach(x => {
                if(x.name == $('#deviceid').val()){


                    map.flyTo({center: [
                            x._lngLat.lng,
                            x._lngLat.lat
                        ]});

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

    if (map.getLayer("overlay")){
        map.removeLayer("overlay");
        console.log("remove overlay");
    }

    if (map.getSource("myImageSource")){
        map.removeSource("myImageSource");
        console.log("remove myImageSource");
    }

    map.setStyle('mapbox://styles/mapbox/' + layerId + '-v9');

    relayer();
});

