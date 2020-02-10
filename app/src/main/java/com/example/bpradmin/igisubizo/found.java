package com.example.bpradmin.igisubizo;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class found extends Fragment {
    public TextView officename, representer,owner, doctype, identifier,payment, province, district, sector, cell, phone, address;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_found, container, false);
        officename = (TextView) view.findViewById(R.id.officename);
        representer = (TextView) view.findViewById(R.id.officerepresenter);
        owner = (TextView) view.findViewById(R.id.owner);
        doctype = (TextView) view.findViewById(R.id.type);
        identifier = (TextView) view.findViewById(R.id.identifier);
        payment = (TextView) view.findViewById(R.id.payment);
        province = (TextView) view.findViewById(R.id.officeprovince);
        district = (TextView) view.findViewById(R.id.officedistrict);
        sector = (TextView) view.findViewById(R.id.officesector);
        cell = (TextView) view.findViewById(R.id.officecell);
        phone = (TextView) view.findViewById(R.id.officetel);
        address = (TextView) view.findViewById(R.id.officeaddr);
        // Inflate the layout for this fragment
        setFragView();
        return view;
    }

    public void setFragView() {
        if (getArguments() != null) {
            try {
                Bundle jsonObject = this.getArguments();
                officename.setText(officename.getText() + " " + jsonObject.getString("name"));
               representer.setText(representer.getText()+" " + jsonObject.getString("representer"));
                owner.setText(owner.getText() + " " + jsonObject.getString("owner"));
                doctype.setText(doctype.getText() + " " + jsonObject.getString("doctype"));
                identifier.setText(identifier.getText() + " " + jsonObject.getString("identifier"));
                payment.setText(payment.getText() + ": "+jsonObject.getString("payamount")+" RWF");
                province.setText(province.getText() + " " + jsonObject.getString("province"));
                district.setText(district.getText() + " " + jsonObject.getString("district"));
                sector.setText(sector.getText() + " " + jsonObject.getString("sector"));
                cell.setText(cell.getText() + " " + jsonObject.getString("cell"));
                phone.setText(phone.getText() + " " + jsonObject.getString("phone"));
                address.setText(address.getText() + " " + jsonObject.get("address"));
                //    Toast.makeText(getActivity(), "Passed " + jsonObject.getString("identifier"), Toast.LENGTH_SHORT).show();
            }catch (Exception ex){
        // Toast.makeText(getActivity(), "Passed " +ex.getMessage(), Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
        }
    }
}
