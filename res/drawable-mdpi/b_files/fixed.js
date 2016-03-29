
/* These help make my things go easy LOL */
function out(something) {
    console.log("Printing :3 " + something);
}

function initFixedChat() {
    // Get the recent chat box and the add the click event.
    var recentChat = document.getElementById('recent-chats');
    out("initFixedChat: Fetching the recent chats ...");
    out(recentChat);

    recentChat.addEventListener('click', function() {
        // Get the list and hide it.
        var recentList = recentChat.getElementsByClassName("list");
        out("Recent list :");
        out(recentList);
        var currentDisplay = recentList.style.display;
        recentList.style.display = currentDisplay == "block" ? "none" : "block";

    });


}

initFixedChat();
