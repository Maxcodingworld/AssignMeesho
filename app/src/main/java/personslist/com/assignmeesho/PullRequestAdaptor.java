package personslist.com.assignmeesho;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import personslist.com.assignmeesho.pojo.PullRequestitem;

/**
 * Created by Max on 08/10/17.
 */

public class PullRequestAdaptor extends RecyclerView.Adapter<PullRequestAdaptor.BaseViewHolder> {

    int type_list_item = 101;
    ArrayList<PullRequestitem> items  = null;
    Context context = null;
    LayoutInflater inflater = null;


    public PullRequestAdaptor(ArrayList<PullRequestitem> items, Context context) {
        this.items = items;
        this.context = context;
        if(context!=null){
            inflater = LayoutInflater.from(context);
        }
    }

    @Override
    public PullRequestAdaptor.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.pull_request_item,parent,false);
        return new ItemViewHolder(view,type_list_item);
    }

    @Override
    public void onBindViewHolder(PullRequestAdaptor.BaseViewHolder holder,final int position) {

        if(holder.getItemViewType() == type_list_item){
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            if(itemViewHolder.userName!=null){
                if(items.get(position)!=null && items.get(position).getUserName()!=null) {
                    itemViewHolder.userName.setText(items.get(position).getUserName());
                }else{
                    itemViewHolder.userName.setText("--");
                }
            }

            if(itemViewHolder.repoTitle!=null){
                if(items.get(position)!=null && items.get(position).getRepoName()!=null && items.get(position).getTitle()!=null) {
                    itemViewHolder.repoTitle.setText(items.get(position).getRepoName() + ":" +  items.get(position).getTitle());
                }else{
                    itemViewHolder.repoTitle.setText("--");
                }
            }

            if(itemViewHolder.avator!=null){
                if(items.get(position)!=null && items.get(position).getAvatorUrl()!=null) {
                    Picasso.with(context).load(items.get(position).getAvatorUrl()).into(itemViewHolder.avator);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if(items == null){
            return 0;
        }else{
            return items.size();
        }
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder{
        public int viewType = type_list_item;
        public View view;
        public BaseViewHolder(View itemView, int type) {
            super(itemView);
            viewType = type;
            view = itemView;
        }
    }

    public class ItemViewHolder extends PullRequestAdaptor.BaseViewHolder {

        ImageView avator = null;
        TextView userName = null;
        TextView repoTitle = null;

        public ItemViewHolder(View itemView,int type) {
            super(itemView,type);
            avator = (ImageView) itemView.findViewById(R.id.avator);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            repoTitle = (TextView) itemView.findViewById(R.id.repo_title);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return type_list_item;
    }
}
