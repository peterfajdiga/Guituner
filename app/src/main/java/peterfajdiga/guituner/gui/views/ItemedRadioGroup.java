package peterfajdiga.guituner.gui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ItemedRadioGroup<ItemType extends ItemedRadioGroup.Item> extends RadioGroup {
    private final Map<Integer, ItemType> itemsMap = new HashMap<>();
    private final Context context;
    private Receiver<ItemType> receiver;

    public ItemedRadioGroup(final Context context) {
        super(context);
        this.context = context;
        initialize();
    }

    public ItemedRadioGroup(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initialize();
    }

    @Override
    public void onViewRemoved(final View child) {
        itemsMap.remove(child.getId());
        super.onViewRemoved(child);
    }

    private void initialize() {
        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup group, final int checkedId) {
                if (receiver == null) {
                    return;
                }
                final ItemType item = itemsMap.get(checkedId);
                if (item == null) {
                    return;
                }
                receiver.onCheckedChanged(item);
            }
        });
    }

    public void setReceiver(@NonNull final Receiver<ItemType> receiver) {
        this.receiver = receiver;
    }

    public void addItem(@NonNull final ItemType item, final boolean checked) {
        final View buttonView = createRadioButton(item);
        addView(buttonView);

        final int id = buttonView.getId();
        itemsMap.put(id, item);
        if (checked) {
            check(id);
        }
    }

    @NonNull
    private View createRadioButton(@NonNull final ItemType item) {
        final RadioButton button = new RadioButton(context);
        button.setText(item.getButtonText());
        return button;
    }

    public interface Item {
        CharSequence getButtonText();
    }

    public interface Receiver<ItemType> {
        void onCheckedChanged(ItemType item);
    }
}
