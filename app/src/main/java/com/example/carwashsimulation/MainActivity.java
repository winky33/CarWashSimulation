package com.example.carwashsimulation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Button startBtn, collectBtn,queueBtn, completeBtn;
    ListView listView;
    TextView queueLength,currentWash,ticket_txt,listTitle;

    QUEUE queue = new QUEUE(10);

    Timer timer;
    TimerTask washTask;

    ArrayList<String> completedList = new ArrayList<>();
    final Handler handler = new Handler();
    int idx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.startQueue);
        collectBtn = findViewById(R.id.collectCar);
        queueBtn = findViewById(R.id.inQueue_btn);
        completeBtn = findViewById(R.id.complete_btn);
        listView = findViewById(R.id.listView);
        ticket_txt = findViewById(R.id.checkTicket_txt);
        queueLength = findViewById(R.id.queue_length);
        currentWash = findViewById(R.id.currentWash);
        listTitle = findViewById(R.id.listTitle);

        completedList.add("WS01");
        completedList.add("WS02");
        ArrayAdapter completedAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,completedList);

        queue.enqueue("WS0293");
        queue.enqueue("WS0233");
        queue.enqueue("WS0403");
        queue.enqueue("WS0620");

        setInQueueAdaptor();

        startBtn.setOnClickListener(view -> {
            if(!checkTicket(ticket_txt.getText().toString())){
                String ticket = GenerateTicket();
                ticket_txt.setText(ticket);
                queue.enqueue(ticket);
                setInQueueAdaptor();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "Already in queue, please wait", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        queueBtn.setOnClickListener(view -> {
            setInQueueAdaptor();
            listTitle.setText("IN QUEUE");
        });

        completeBtn.setOnClickListener(view -> {
            listView.setAdapter(completedAdapter);
            listTitle.setText("PENDING TO PICKUP");
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //onResume we start our timer so it can start when the app comes from the background
        startTimer();
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(washTask, 5000, 50000);
    }

    public void stoptimertask(View v) {
        //stop the timer, if it's not already null
        if (timer == null) {
            timer.cancel();
        }
    }

    public void initializeTimerTask() {

        washTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        String currentTitle = "Current Washing : ";
                        String[] currentQueue = queue.queueList();
                        String current = currentQueue[idx];

                        if(current!=null){
                            currentWash.setText(currentTitle.concat(current));

                            String completed = queue.dequeue();
                            setInQueueAdaptor();

                            completedList.add(completed);
                        }

                        idx++;
                    }
                });
            }
        };
    }

    public String[] RemoveNullValue (String[] queue){
        String[] checkNull = queue;
        List<String> list = new ArrayList<String>();
        for(String s : checkNull) {
            if(s != null && s.length() > 0) {
                list.add(s);
            }
        }
        checkNull = list.toArray(new String[list.size()]);

        return checkNull;
    }

    public String GenerateTicket(){
        String obj = "CWQ";
        Random random = new Random();
        int number = random.nextInt(1000);

        String ticket = obj.concat(String.valueOf(number));

        return ticket;
    }

    public boolean checkTicket(String ticket){
        String[] list = queue.queueList();

        for(String t : list){
            if(t == ticket){
                return true;
            }
        }
        return false;
    }

    public void setInQueueAdaptor(){
        String[] inQueue = RemoveNullValue(queue.queueList());

        Log.i("QueueList", String.valueOf(inQueue[1]));

        ArrayAdapter inQueueAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, inQueue);
        listView.setAdapter(inQueueAdapter);
    }
}

interface IQueuable {
    //adds value to queue and returns new queue
    String[] enqueue(String value);

    //removes item from queue, and returns the item removed
    String dequeue();

    //returns the number of item in the queue
    int size();

    String[] queueList();
}

class QUEUE implements IQueuable{
    String[] queue;
    int maxsize;
    int size;
    int front;
    int rear;

    QUEUE(int max_size) {
        this.queue = new String[max_size];
        this.size = 0;
        this.front = 0;
        this.rear = -1;
    }

    public String[] enqueue(String value){
        if(maxsize == size){//the queue is full
            String toast = "the queue is full";
        }
        if(rear == maxsize-1){
            rear = -1;
        }
        queue[++rear] = value;
        size++;

        return queue;
    }

    public String dequeue(){

        if(size == 0){//the queue is empty
            String toast = "the queue is empty";
        }

        String removed_item = queue[front++];
        if(front == maxsize){
            front = 0;
        }
        size--;

        return removed_item;
    }

    public int size(){
        return size;
    }

    public String[] queueList(){
        return queue;
    }

}
