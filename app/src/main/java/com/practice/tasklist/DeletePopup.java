package com.practice.tasklist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DeletePopup extends AppCompatDialogFragment {

    private DeletePopupListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.popup_delete, null);

        builder.setView(view)
                .setTitle("")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.getAction("delete");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.getAction("cancel");
                    }

                });

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DeletePopupListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + " must implement DeletePopupListener!");
        }
    }

    public interface DeletePopupListener {
        void getAction(String action);
    }




}
