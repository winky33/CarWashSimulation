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
                        String queueNoTitle = "Queue Length : ";
                        ArrayList<String> currentQueue = queue.queueList();

                        try{
                            String current = currentQueue.get(idx);
                            int queueNo = queue.size();

                            Log.i("ArrayItem",current);

                            if(current!=null){
                                currentWash.setText(currentTitle.concat(current));
                                queueLength.setText(queueNoTitle.concat(Integer.toString(queueNo-1)));

                                String completed = queue.dequeue();
                                setInQueueAdaptor();

                                completedList.add(completed);
                            }

                        }catch(Exception exception){
                            currentWash.setText(currentTitle.concat("none"));
                            queueLength.setText(queueNoTitle.concat(Integer.toString(0)));
                            timer = null;
                        }
                    }
                });
            }
        };
    }

    public String GenerateTicket(){
        String obj = "CWQ";
        Random random = new Random();
        int number = random.nextInt(1000);

        String ticket = obj.concat(String.valueOf(number));

        return ticket;
    }

    public boolean checkTicket(String ticket){
        ArrayList<String> list = queue.queueList();

        for(String t : list){
            if(t == ticket){
                return true;
            }
        }
        return false;
    }

    public void setInQueueAdaptor(){
        ArrayList<String> inQueue = queue.queueList();

        ArrayAdapter inQueueAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, inQueue);
        listView.setAdapter(inQueueAdapter);
    }
}

interface IQueuable {
    //adds value to queue and returns new queue
    ArrayList<String> enqueue(String value);

    //removes item from queue, and returns the item removed
    String dequeue();

    //returns the number of item in the queue
    int size();

    ArrayList<String> queueList();
}

class QUEUE implements IQueuable{
    ArrayList<String> queue;
    int size;

    QUEUE(int max_size) {
        this.queue = new ArrayList<>();
        this.size = 0;
    }

    public ArrayList<String> enqueue(String value){
        queue.add(value);

        return queue;
    }

    public String dequeue(){
        String removed_item = queue.get(0);
        queue.remove(0);

        return removed_item;
    }

    public int size(){
        return queue.size();
    }

    public ArrayList<String> queueList(){
        return queue;
    }

}
