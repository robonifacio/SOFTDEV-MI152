package apc.edu.ph.playsafe;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;



public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.SearchViewHolder> {

    Context ctx;
    ArrayList<String> tnumberList;
    ArrayList<String> buyernList;
    ArrayList<String> sellernList;
    ArrayList<String> priceList;
    public String data;

    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView ttext;
        TextView btext;
        TextView stext;
        TextView ptext;

        public SearchViewHolder(View itemView){
            super(itemView);
            ttext = (TextView) itemView.findViewById(R.id.ttext);
            btext = (TextView) itemView.findViewById(R.id.btext);
            stext = (TextView) itemView.findViewById(R.id.stext);
            ptext = (TextView) itemView.findViewById(R.id.ptext);
        }

    }

    public TransactionAdapter(Context ctx, ArrayList<String> tnumberList,ArrayList<String> buyernList, ArrayList<String> sellernList, ArrayList<String> priceList) {
        this.ctx = ctx;
        this.tnumberList = tnumberList;
        this.buyernList = buyernList;
        this.sellernList = sellernList;
        this.priceList = priceList;
    }

    @Override
    public TransactionAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.listview_layout, parent, false);
        return new TransactionAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder searchViewHolder, final int position) {

        searchViewHolder.ttext.setText(tnumberList.get(position));
        searchViewHolder.btext.setText(buyernList.get(position));
        searchViewHolder.stext.setText(sellernList.get(position));
        searchViewHolder.ptext.setText(priceList.get(position));

        searchViewHolder.btext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = buyernList.get(position).toString();
                Toast.makeText(ctx, value, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return buyernList.size();
    }

}
