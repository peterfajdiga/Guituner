package peterfajdiga.guituner.gui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

// TODO: consider using ListView or RecyclerView instead
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

    public void addItem(
            @NonNull final ItemType item,
            @LayoutRes final int buttonResource,
            final boolean checked,
            final boolean clickable
    ) throws IllegalArgumentException {
        final View buttonView = createRadioButton(item, buttonResource);
        addView(buttonView);

        final int id = buttonView.getId();
        itemsMap.put(id, item);
        if (checked) {
            check(id);
        }

        if (clickable) {
            buttonView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    receiver.onClick(item);
                }
            });
        }
    }

    @NonNull
    private View createRadioButton(
            @NonNull final ItemType item,
            @LayoutRes final int buttonResource
    ) throws IllegalArgumentException {
        final LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View inflatedView = layoutInflater.inflate(buttonResource, this, false);
        if (!(inflatedView instanceof RadioButton)) {
            throw new IllegalArgumentException("buttonResource is not a RadioButton layout resource");
        }
        final RadioButton button = (RadioButton)inflatedView;
        button.setText(item.getButtonText());
        return button;
    }

    public interface Item {
        CharSequence getButtonText();
    }

    public interface Receiver<ItemType> {
        void onCheckedChanged(ItemType item);
        void onClick(ItemType item);
    }
}
