package com.example.nlcs_app;

        import android.graphics.Xfermode;
        import android.graphics.drawable.Drawable;
        import android.text.Layout;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.Set;

public class Adapter_Chat extends RecyclerView.Adapter<Adapter_Chat.MessageViewHolder>{

    private HashMap<Integer, Map<String, String>> listMessage;

    public Adapter_Chat(HashMap<Integer, Map<String,String>> listMessage) {
        this.listMessage = listMessage;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view   = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mess,parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {


        Log.d( "position", "onBindViewHolder: " + position);

        String src      = "";
        String message  = "";
        if( listMessage.get(position) != null){

            Set set = listMessage.keySet();
            int i = 0, j = 0;
            for (Object key : set) {
                Set set2 = listMessage.get(key).keySet();
                if( i == position){
                    for (Object key2 : set2) {
                        src     =   key2.toString();
                        message =   listMessage.get(key).get(key2);
                        Log.d( "src", "onBindViewHolder: " + src);
                        break;
                    }
                }
                i++;
            }

            if( src != null ){
                if( src.compareTo("my") == 0) {
                    holder.textViewMessage.setText(message);
                    holder.layout.setGravity(Gravity.END);
                    holder.textViewMessage.setBackgroundResource(R.drawable.bg_item_message);
                } else { // key == "fr"
                    holder.layout.setGravity(Gravity.START);
                    holder.textViewMessage.setBackgroundResource(R.drawable.background_teal_fr);
                    holder.textViewMessage.setText(message);
                }

            }
        }

    }

    public void setChangeList( HashMap<Integer, Map<String, String>> listMessage) {
        this.listMessage.putAll(listMessage);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if( listMessage != null)
        {
            return listMessage.size();
        }
        return 0;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewMessage;
        private LinearLayout layout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            layout          = itemView.findViewById(R.id.layout);
        }

    }
}
