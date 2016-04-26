package foocoder.dnd.presentation.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import foocoder.dnd.R;
import foocoder.dnd.domain.Contact;
import foocoder.dnd.presentation.internal.di.PerActivity;
import foocoder.dnd.views.RoundedLetterView;

@PerActivity
public class ContactAdapter extends BaseAdapter {

    private Activity context;

    private List<Contact> contacts;

    @Inject
    public ContactAdapter(Activity context, List<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.contacts_list_item, parent, false);
            viewHolder.tv = (TextView) convertView.findViewById(R.id.tv);
            viewHolder.iv = (RoundedLetterView) convertView.findViewById(R.id.contact);
            viewHolder.check = (CheckBox) convertView.findViewById(R.id.check);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Contact contact = contacts.get(position);
        viewHolder.iv.setTitleText(contact.name.substring(0, 1));
        viewHolder.iv.setBackgroundColor(contact.color);
        viewHolder.tv.setText(contact.name);
        viewHolder.check.setChecked(contact.selected);

        return convertView;
    }

    public void changeData(List<Contact> contactList) {
//        contacts.clear();
//        contacts.addAll(contactList);
        contacts = contactList;
    }

    private static class ViewHolder {
        TextView tv;
        RoundedLetterView iv;
        CheckBox check;
    }

//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
//        View view = LayoutInflater.from(context).inflate(R.layout.contacts_list_item, viewGroup, false);
//
//        MyViewHolder holder = new MyViewHolder(view);
//
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
//        MyViewHolder holder = (MyViewHolder) viewHolder;
//        Contact contact = contacts.get(i);
//
////        byte[] photo = contact.getPhoto();
////        if (contact.getPhotoSize() != 0) {
////            holder.iv.setImageBitmap(BitmapFactory.decodeByteArray(photo, 0, photo.length));
//            holder.iv.setTitleText(contact.getName().substring(0, 1));
////        } else {
////            holder.iv.setImageResource(R.drawable.photo);
////        }
//        holder.tv.setText(contact.toString());
//        holder.check.setChecked(contact.isSelected());
//    }
//
//    @Override
//    public int getItemCount() {
//        return contacts.size();
//    }
//
//    public class MyViewHolder extends RecyclerView.ViewHolder {
//
//        public TextView tv;
//        public RoundedLetterView iv;
//        public CheckBox check;
//
//        public MyViewHolder(View itemView) {
//            super(itemView);
//            tv = (TextView) itemView.findViewById(R.id.tv);
//            iv = (RoundedLetterView) itemView.findViewById(R.id.contact);
//            check = (CheckBox) itemView.findViewById(R.id.check);
//        }
//    }
}
