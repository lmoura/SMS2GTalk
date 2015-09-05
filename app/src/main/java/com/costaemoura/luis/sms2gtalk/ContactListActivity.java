package com.costaemoura.luis.sms2gtalk;

import java.util.ArrayList;
import java.util.Collection;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ContactListActivity extends Activity implements OnItemClickListener {

    ListView listView;
    Roster roster;
    ArrayList<Contact> contactList;
    Resources res;
    ArrayAdapter<Contact> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);

        listView = (ListView) findViewById(R.id.listViewContacts);
        roster = ConnectionHelper.getRoster();
        res = getResources();
        contactList = ConnectionHelper.getContactList(res);
        adapter = new ContactArrayAdapter(ContactListActivity.this, R.layout.list_item, contactList);
        listView.setAdapter(adapter);
        listView.setItemsCanFocus(true);
        listView.setClickable(true);
        listView.setOnItemClickListener(this);

        roster.addRosterListener(new RosterListener() {

            @Override
            public void presenceChanged(Presence arg0) {
                updateUser(arg0);
            }

            @Override
            public void entriesUpdated(Collection<String> arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void entriesDeleted(Collection<String> arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void entriesAdded(Collection<String> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void updateUser(Presence presence) {
        boolean newUser = true;
        Contact removeC = null;
        for (Contact contact : contactList) {
            if (contact.getRosterEntry().getUser().equals(StringUtils.parseBareAddress(presence.getFrom()))) {
                newUser = false;
                if (presence.isAvailable() || presence.isAway()) {
                    contact.setStatus(presence.getStatus());
                    if (presence.getMode() == null) {
                        contact.setImage(res.getDrawable(R.mipmap.available));
                    } else if (presence.getMode().equals(Presence.Mode.chat)) {
                        contact.setImage(res.getDrawable(R.mipmap.available));
                    } else if (presence.getMode().equals(Presence.Mode.away) || presence.getMode().equals(Presence.Mode.xa)) {
                        contact.setImage(res.getDrawable(R.mipmap.idle));
                    } else if (presence.getMode().equals(Presence.Mode.dnd)) {
                        contact.setImage(res.getDrawable(R.mipmap.busy));
                    } else {
                        contact.setImage(res.getDrawable(R.mipmap.available));
                    }
                    new UpdateContact().execute();
                } else {
                    removeC = contact;
                }
                break;
            }
        }
        if (removeC != null) {
            /*adapter.remove(removeC);
			adapter.notifyDataSetChanged();*/
            new DeleteContact().execute(removeC);
        }
        if (newUser) {
            RosterEntry re = roster.getEntry(StringUtils.parseBareAddress(presence.getFrom()));
            Contact c = new Contact();
            if (re.getName() == null || re.getName().trim().equals("")) {
                c.setName(re.getUser());
            } else {
                c.setName(re.getName());
            }
            c.setStatus(presence.getStatus());
            c.setRosterEntry(re);
            if (presence.getMode() == null) {
                c.setImage(res.getDrawable(R.mipmap.available));
            } else if (presence.getMode().equals(Presence.Mode.chat)) {
                c.setImage(res.getDrawable(R.mipmap.available));
            } else if (presence.getMode().equals(Presence.Mode.away)
                    || presence.getMode().equals(Presence.Mode.xa)) {
                c.setImage(res.getDrawable(R.mipmap.idle));
            } else if (presence.getMode().equals(Presence.Mode.dnd)) {
                c.setImage(res.getDrawable(R.mipmap.busy));
            } else {
                c.setImage(res.getDrawable(R.mipmap.available));
            }
			/*adapter.add(c);
			adapter.notifyDataSetChanged();*/
            new UpdateContact().execute(c);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectionHelper.getConnection().disconnect();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        Intent in = new Intent(ContactListActivity.this, ChatActivity.class);
        in.putExtra("user", contactList.get(position).getRosterEntry().getUser());
        startActivity(in);
        //RosterEntry re = contact_list.get(position).getRosterEntry();
        //Toast.makeText(ContactListActivity.this, contact_list.get(position).getName(), Toast.LENGTH_SHORT).show();
		/*ChatManager chatManager = ConnectionHelper.getConnection().getChatManager(); 
		Chat chat = chatManager.createChat(re.getUser(), new MessageListener() {
			
			@Override
			public void processMessage(Chat arg0, Message arg1) {
				// TODO Auto-generated method stub
				
			}
		});*/
		/*try {
			chat.sendMessage("Hello");
		} catch (XMPPException e) {
			e.printStackTrace();
		}*/
    }

    class UpdateContact extends AsyncTask<Contact, Void, Void> {

        @Override
        protected Void doInBackground(Contact... contacts) {
            if (contacts != null && contacts.length > 0) {
                for (Contact c : contacts) {
                    contactList.add(c);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter.notifyDataSetChanged();
        }


    }

    class DeleteContact extends AsyncTask<Contact, Void, Void> {

        @Override
        protected Void doInBackground(Contact... contacts) {
            if (contacts != null && contacts.length > 0) {
                for (Contact c : contacts) {
                    contactList.remove(c);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter.notifyDataSetChanged();
        }

    }
}
