package main

import "fmt"
import "time"
import "image"
import _ "image/jpeg"
import "net"
import "bufio"
//import "os"

func main(){
	fmt.Println("Server starting up...")
	l, err := net.Listen("tcp", ":1101")
	if err != nil{
		fmt.Println("Server error, cannot listen")
		return
	}
	defer l.Close()
	
	fmt.Println("Online!")
	for{
		conn, err := l.Accept()
		if err != nil{
			fmt.Println("Error accepting")
			return
		}
		go processClient(conn)
	}
}

func processClient(conn net.Conn){
	//may have to call a mutex on this for safety. or maybe not cuz lazy
	//buffer := make([]byte, 1024)
	//writer := io.Writer
	var snapshot image.Image
	var err error
	var start time.Time
	var format string
	reader := bufio.NewReader(conn)
	buffer := make([]byte, 2048) //will need adjusted based on size
	for{
		start = time.Now()
		reader.Read(buffer)
		snapshot, format, err = image.Decode(reader)
		if err != nil{
			fmt.Println(err)
			break
		}
		fmt.Println(time.Since(start))
		//fmt.Printf("%s image verified! ", format)
		//fmt.Println(snapshot.Bounds())
	}
	_, _ = snapshot, format
	/*for{
		n, err := conn.Read(buffer)
		//feed into stream until 0 bytes read
		if err != nil{
			fmt.Println("Error reading bytes from socket")
			return
		}
		fmt.Println(buffer, n)
		return
		if n == 0{ //no bytes left
			break
		}
		fmt.Println()
		start := time.Now()
		//verify image
		fmt.Println(time.Now().Sub(start))
		return
	}*/
	conn.Close()
}