package peterfajdiga.guituner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import peterfajdiga.guituner.fourier.Recorder;
import peterfajdiga.guituner.fourier.Transformator;

public class TunerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuner);

        final Transformator fourier = new Transformator();
        fourier.startTransforming();
        final Recorder recorder = new Recorder(fourier);
        recorder.startRecording();

        final TextView hello = (TextView)findViewById(R.id.hello);
        hello.setText("sadf");
    }
}
