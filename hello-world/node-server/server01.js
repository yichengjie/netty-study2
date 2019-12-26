require('net').createServer(function (sock) {
    sock.on('data',function (data) {
        func3(sock) ;
    })
}).listen(8989,'127.0.0.1') ;


function func1(sock) {
    sock.write('HTTP/1.1 200 OK\r\n') ;
    sock.write('\r\n') ;
    sock.write('hello world!') ;
    sock.destroy();
}

function func2(sock) {
    sock.write('HTTP/1.1 200 OK\r\n');
    sock.write('Content-Length: 12\r\n');
    sock.write('\r\n');
    sock.write('hello world!');
}

function func3(sock) {
    sock.write('HTTP/1.1 200 OK\r\n');
    sock.write('Transfer-Encoding: chunked\r\n');
    sock.write('\r\n');

    //第一行表示长度，第二行表示内容
    sock.write('b\r\n');
    sock.write('01234567890\r\n');
    //第一行表示长度，第二行表示内容
    sock.write('5\r\n');
    sock.write('67867\r\n');
    //第一行表示长度，第二行表示内容
    sock.write('3\r\n');
    sock.write('678\r\n');
    //第一行表示长度，第二行表示内容
    sock.write('2\r\n');
    sock.write('56\r\n');
    //第一行表示长度，第二行表示内容
    sock.write('0\r\n');
    sock.write('\r\n');
}