var stompClient = null;
var playerId = null;
var socket = null;

function setConnected(connected) {
    $("#connected").hide();
    $("#disconnected").hide();
    if (connected) {
        $("#connected").show();
    }
    else {
        $("#disconnected").show();
    }
}

function connect() {
    socket = new SockJS('/playerDepositSocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        startConsume();
        stompClient.subscribe('/topic/depositAggregation/'+playerId, function (PlayerDeposit) {
            var jsonResponse = JSON.parse(PlayerDeposit.body);
            $("#aggregateDeposits").html("<tr><td>" + jsonResponse.amount + "</td></tr>");
        });
    }, function(message) {
        if (stompClient !== null) {
            stompClient.disconnect();
        }
        console.log(message);
        setConnected(false);
        connect();
    });

}

function startConsume() {
    stompClient.send("/app/ConsumeAggDepositsPerPlayer");
}

function deposit() {
    if(playerId === null)
    {
        alert("Error no playerId");
        return;
    }
    var amount = $("#amount").val();
    if(amount === null || amount <= 0 || isNaN(amount))
    {
        alert("Error invalid amount");
        return;
    }
    stompClient.send("/app/playerDeposit", {}, JSON.stringify({'amount': amount , 'playerId' : playerId}));
    $("#amount").val('');
}

function generatePlayerId()
{
    let searchParams = new URLSearchParams(window.location.search)
    if(searchParams.has('pid') && searchParams.get('pid') != '')
    {
        playerId = searchParams.get('pid');
    }
    else
    {
        playerId = Math.floor(Math.random()*10000) + 1000;
    }

    $("#playerId").html(playerId);
}

$(function () {
    setConnected(false);
    generatePlayerId();
    connect();
    $( "#deposit" ).click(function() { (deposit()); });
});