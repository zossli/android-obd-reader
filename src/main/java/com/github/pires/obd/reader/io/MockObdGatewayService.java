package com.github.pires.obd.reader.io;

import android.util.Log;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.reader.activity.MainActivity;
import com.github.pires.obd.reader.io.ObdCommandJob.ObdCommandJobState;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

/**
 * This service is primarily responsible for establishing and maintaining a
 * permanent connection between the device where the application runs and a more
 * OBD Bluetooth interface.
 * <p/>
 * Secondarily, it will serve as a repository of ObdCommandJobs and at the same
 * time the application state-machine.
 */
public class MockObdGatewayService extends AbstractGatewayService {

    private static final String TAG = MockObdGatewayService.class.getName();
    private Random random = new Random();

    public void startService() {
        Log.d(TAG, "Starting " + this.getClass().getName() + " service..");

        // Let's configure the connection.
        Log.d(TAG, "Queing jobs for connection configuration..");
        queueJob(new ObdCommandJob(new ObdResetCommand()));
        queueJob(new ObdCommandJob(new EchoOffCommand()));

    /*
     * Will send second-time based on tests.
     *
     * TODO this can be done w/o having to queue jobs by just issuing
     * command.run(), command.getResult() and validate the result.
     */
        //queueJob(new ObdCommandJob(new EchoOffCommand()));
        //queueJob(new ObdCommandJob(new LineFeedOffCommand()));
       // queueJob(new ObdCommandJob(new TimeoutCommand(62)));

        // For now set protocol to AUTO
        //queueJob(new ObdCommandJob(new SelectProtocolCommand(ObdProtocols.AUTO)));

        // Job for returning dummy data
        //queueJob(new ObdCommandJob(new AmbientAirTemperatureCommand()));
        queueJob(new ObdCommandJob(new SpeedCommand()));


        queueCounter = 0L;
        Log.d(TAG, "Initialization jobs queued.");

        isRunning = true;
    }


    /**
     * Runs the queue until the service is stopped
     */
    protected void executeQueue() {
        Log.d(TAG, "Executing queue..");
        while (!Thread.currentThread().isInterrupted()) {
            ObdCommandJob job = null;
            try {
                job = jobsQueue.take();

                Log.d(TAG, "Taking job[" + job.getId() + "] from queue..");

                if (job.getState().equals(ObdCommandJobState.NEW)) {
                    Log.d(TAG, "Job state is NEW. Run it..");
                    job.setState(ObdCommandJobState.RUNNING);
                    Log.d(TAG, job.getCommand().getName());

                    switch (job.getCommand().getName()){
                        case "Vehicle Speed":
                            String speed = Integer.toHexString(random.nextInt(199)+1);
                            String speedHex = (speed.length()==1)?"0"+speed:speed;
                            job.getCommand().run(new ByteArrayInputStream(("01 0D "+ speedHex.toUpperCase()+" 00>").getBytes()), new ByteArrayOutputStream());
                            break;
                        case "Throttle Position":
                            String throttle = Integer.toHexString(random.nextInt(254)+1);
                            String throttleHex = (throttle.length()==1)?"0"+throttle:throttle;
                            job.getCommand().run(new ByteArrayInputStream(("01 11 "+throttleHex.toUpperCase()+" 12>").getBytes()), new ByteArrayOutputStream());
                            break;
                        case "Engine Load":
                            String engine = Integer.toHexString(random.nextInt(254)+1);
                            String engingeHex = (engine.length()==1)?"0"+engine:engine;
                            job.getCommand().run(new ByteArrayInputStream(("01 11 "+engingeHex.toUpperCase()+" 12>").getBytes()), new ByteArrayOutputStream());
                            break;
                        case "Fuel Level":
                            String fuel = Integer.toHexString(random.nextInt(184)+40);
                            String fuelHex = (fuel.length()==1)?"0"+fuel:fuel;
                            job.getCommand().run(new ByteArrayInputStream(("01 2F "+fuelHex.toUpperCase()+" 12>").getBytes()), new ByteArrayOutputStream());
                            break;
                        case "Engine Coolant Temperature":
                            String enginetemp = Integer.toHexString(random.nextInt(184)+40);
                            String enginetempHex = (enginetemp.length()==1)?"0"+enginetemp:enginetemp;
                            job.getCommand().run(new ByteArrayInputStream(("01 05 "+enginetempHex.toUpperCase()+" 12>").getBytes()), new ByteArrayOutputStream());
                            break;
                        case "Air Intake Temperature":
                            String airTemp = Integer.toHexString(random.nextInt(104)+40);
                            String airTempHex = (airTemp.length()==1)?"0"+airTemp:airTemp;
                            job.getCommand().run(new ByteArrayInputStream(("01 05 "+airTempHex.toUpperCase()+" 12>").getBytes()), new ByteArrayOutputStream());
                            break;
                        case "Engine oil temperature":
                            String oilTemp = Integer.toHexString(random.nextInt(184)+40);
                            String oilTempHex = (oilTemp.length()==1)?"0"+oilTemp:oilTemp;
                            job.getCommand().run(new ByteArrayInputStream(("01 05 "+oilTempHex.toUpperCase()+" 12>").getBytes()), new ByteArrayOutputStream());
                            break;
                        case "Control Module Power Supply ":
                            String contrVol = Integer.toHexString(random.nextInt(254)+1);
                            String ContrVolHex = (contrVol.length()==1)?"0"+contrVol:contrVol;
                            String contrVol1 = Integer.toHexString(random.nextInt(28)+28);
                            String ContrVolHex1 = (contrVol1.length()==1)?"0"+contrVol1:contrVol1;
                            job.getCommand().run(new ByteArrayInputStream(("01 05 "+ContrVolHex1.toUpperCase()+" " +ContrVolHex.toUpperCase()+">").getBytes()), new ByteArrayOutputStream());
                            break;
                        case "Engine RPM":
                            String rpm1 = Integer.toHexString(random.nextInt(120)+1);
                            String rpm2 = Integer.toHexString(random.nextInt(253)+1);
                            String rpm1Hex = (rpm1.length()==1)?"0"+rpm1:rpm1;
                            String rpm2Hex = (rpm1.length()==1)?"0"+rpm2:rpm2;
                            job.getCommand().run(new ByteArrayInputStream(("01 11 "+rpm1Hex.toUpperCase()+" "+ rpm2Hex.toUpperCase() +">").getBytes()), new ByteArrayOutputStream());
                            break;
                        default:
                            job.getCommand().run(new ByteArrayInputStream("41 00 00 00>41 00 00 00>41 00 00 00>".getBytes()), new ByteArrayOutputStream());
                            break;

                    }

                } else {
                    Log.e(TAG, "Job state was not new, so it shouldn't be in queue. BUG ALERT!");
                }
            } catch (InterruptedException i) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
                if (job != null) {
                    job.setState(ObdCommandJobState.EXECUTION_ERROR);
                }
                Log.e(TAG, "Failed to run command. -> " + e.getMessage());
            }

            if (job != null) {
                Log.d(TAG, "Job is finished.");
                job.setState(ObdCommandJobState.FINISHED);
                final ObdCommandJob job2 = job;
                ((MainActivity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) ctx).stateUpdate(job2);
                    }
                });

            }
        }
    }


    /**
     * Stop OBD connection and queue processing.
     */
    public void stopService() {
        Log.d(TAG, "Stopping service..");

        notificationManager.cancel(NOTIFICATION_ID);
        jobsQueue.clear();
        isRunning = false;

        // kill service
        stopSelf();
    }

}
