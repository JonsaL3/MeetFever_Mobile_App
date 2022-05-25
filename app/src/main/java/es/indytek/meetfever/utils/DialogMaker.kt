/*
* Author: Julio Landázuri Díaz
* Date: 31/03/2022
* */

package es.indytek.meetfever.utils

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.Button
import android.widget.TextView
import es.indytek.meetfever.R


/// Ejemplo funcion DialogMaker
/*
        DialogMaker(context = yourContext, title = "yourTitle", message = "yourMessage").warningCustomActions(
            customAcceptText = "yourCustomAcceptText", //You can remove this line if you want the default text in your strings.xml file.
            customCancelText = "yourCustomCancelText", //You can remove this line if you want the default text in your strings.xml file.
            customActions = object : DialogCustomActionInterface{
                override fun acceptButton() {
                    //Your code
                }

                override fun cancelButton() {
                    //Your code
                }
            })
 */
class DialogMaker(
    private val context: Context,
    private val title: String = context.getString(R.string.no_text_has_been_passed),
    private val message: String = context.getString(R.string.no_text_has_been_passed)
) {

    //Info layout minimum components:
    //Tittle (textView -> ID: text_info_title), body (textView -> ID: text_info_message), acceptBtn (Button -> ID: btn_info_accept).
    private val infoLayout = R.layout.info_dialog

    //Info layout minimum components:
    //Tittle (textView -> ID: text_info_message).
    private val loadingInfoLayout = R.layout.info_loading_dialog

    //Default background for all dialogs.
    private val background = R.drawable.ic_dialog_bg

    //Warning layout minimum components:
    //Tittle (textView -> ID: text_warning_title), body (textView -> ID: text_warning_message), acceptBtn (Button -> ID: btn_warning_cancel), cancelBtn (Button -> ID: btn_warning_accept).
    private val warningLayout = R.layout.warning_dialog

    //Buttons texts:
    //This class needs the string.xml file, if you want to use other source of default text, change here.
    private val acceptText = context.getString(R.string.dialog_accept)
    private val cancelText = context.getString(R.string.dialog_cancel)

    //Default loading dialog message.
    //This class needs the string.xml file, if you want to use other source of default text, change here.
    private val defaultLoadingText = context.getString(R.string.default_loading_text)

    //Info Dialog with no custom acctions:

    fun infoNoCustomActions(
        customAcceptText: String = acceptText
    ) {

        //Dialog
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(background)
        dialog.setCancelable(false)

        //Added layout to the dialog.
        dialog.setContentView(infoLayout)

        //Added title to the dialog.
        val titleText = dialog.findViewById(R.id.text_info_title) as TextView
        titleText.text = title

        //Added message to the dialog.
        val messageText = dialog.findViewById(R.id.text_info_message) as TextView
        messageText.text = message

        val acceptBtn = dialog.findViewById(R.id.btn_info_accept) as Button
        acceptBtn.text = customAcceptText

        acceptBtn.setOnClickListener {

            dialog.dismiss()

        }

        dialog.show()

    }

    //Info Loading Dialog: you need to hide the dialog with the method dismiss()
    fun infoLoading(
        customText: String = defaultLoadingText
    ): Dialog{
        //Dialog
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(background)
        dialog.setCancelable(false)

        //Added layout to the dialog.
        dialog.setContentView(loadingInfoLayout)

        //Added message to the dialog.
        val titleText = dialog.findViewById(R.id.text_info_message) as TextView
        titleText.text = customText

        dialog.show()

        return dialog
    }

    //Info Dialog with accept button custom action:
    fun infoCustomAccept(
        customAcceptText: String = acceptText,
        customAccept: DialogAcceptCustomActionInterface
    ) {

        //Dialog
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(background)
        dialog.setCancelable(false)

        //Added layout to the dialog.
        dialog.setContentView(infoLayout)

        //Added title to the dialog.
        val titleText = dialog.findViewById(R.id.text_info_title) as TextView
        titleText.text = title

        //Added message to the dialog.
        val messageText = dialog.findViewById(R.id.text_info_message) as TextView
        messageText.text = message

        val acceptBtn = dialog.findViewById(R.id.btn_info_accept) as Button
        acceptBtn.text = customAcceptText

        acceptBtn.setOnClickListener {

            customAccept.acceptButton()

            dialog.dismiss()

        }

        dialog.show()

    }

    //Warning Dialog with no custom actions:

    fun warningNoCustomActions(
        customAcceptText: String = acceptText,
        customCancelText: String = cancelText
    ) {

        //Dialog
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(background)
        dialog.setCancelable(false)

        //Added layout to the dialog.
        dialog.setContentView(warningLayout)

        //Added title to the dialog.
        val titleText = dialog.findViewById(R.id.text_warning_title) as TextView
        titleText.text = title

        //Added message to the dialog.
        val messageText = dialog.findViewById(R.id.text_warning_message) as TextView
        messageText.text = message

        val acceptBtn = dialog.findViewById(R.id.btn_warning_accept) as Button
        acceptBtn.text = customAcceptText

        val cancelBtn = dialog.findViewById(R.id.btn_warning_cancel) as TextView
        cancelBtn.text = customCancelText

        acceptBtn.setOnClickListener {

            dialog.dismiss()

        }

        cancelBtn.setOnClickListener {

            dialog.dismiss()

        }

        dialog.show()

    }

    //Warning Dialog with both custom actions (cancel, accept):

    fun warningCustomActions(
        customAcceptText: String = acceptText,
        customCancelText: String = cancelText,
        customActions: DialogCustomActionInterface
    ) {

        //Dialog
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(background)
        dialog.setCancelable(false)

        //Added layout to the dialog.
        dialog.setContentView(warningLayout)

        //Added title to the dialog.
        val titleText = dialog.findViewById(R.id.text_warning_title) as TextView
        titleText.text = title

        //Added message to the dialog.
        val messageText = dialog.findViewById(R.id.text_warning_message) as TextView
        messageText.text = message

        val acceptBtn = dialog.findViewById(R.id.btn_warning_accept) as Button
        acceptBtn.text = customAcceptText

        val cancelBtn = dialog.findViewById(R.id.btn_warning_cancel) as TextView
        cancelBtn.text = customCancelText

        acceptBtn.setOnClickListener {

            customActions.acceptButton()

            dialog.dismiss()

        }

        cancelBtn.setOnClickListener {

            customActions.cancelButton()

            dialog.dismiss()

        }

        dialog.show()

    }

    //Warning Dialog with accept button custom action:

    fun warningAcceptCustomAction(
        customAcceptText: String = acceptText,
        customCancelText: String = cancelText,
        customAcceptAction: DialogAcceptCustomActionInterface
    ) {

        //Dialog
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(background)
        dialog.setCancelable(false)

        //Added layout to the dialog.
        dialog.setContentView(warningLayout)

        //Added title to the dialog.
        val titleText = dialog.findViewById(R.id.text_warning_title) as TextView
        titleText.text = title

        //Added message to the dialog.
        val messageText = dialog.findViewById(R.id.text_warning_message) as TextView
        messageText.text = message

        val acceptBtn = dialog.findViewById(R.id.btn_warning_accept) as Button
        acceptBtn.text = customAcceptText

        val cancelBtn = dialog.findViewById(R.id.btn_warning_cancel) as TextView
        cancelBtn.text = customCancelText

        acceptBtn.setOnClickListener {

            customAcceptAction.acceptButton()

            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {


            dialog.dismiss()

        }

        dialog.show()

    }

    //Warning Dialog with cancel button custom action:

    fun warningCancelCustomAction(
        customAcceptText: String = acceptText,
        customCancelText: String = cancelText,
        customCancelAction: DialogCancelCustomActionInterface
    ) {

        //Dialog
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(background)
        dialog.setCancelable(false)

        //Added layout to the dialog.
        dialog.setContentView(warningLayout)

        //Added title to the dialog.
        val titleText = dialog.findViewById(R.id.text_warning_title) as TextView
        titleText.text = title

        //Added message to the dialog.
        val messageText = dialog.findViewById(R.id.text_warning_message) as TextView
        messageText.text = message

        val acceptBtn = dialog.findViewById(R.id.btn_warning_accept) as Button
        acceptBtn.text = customAcceptText

        val cancelBtn = dialog.findViewById(R.id.btn_warning_cancel) as TextView
        cancelBtn.text = customCancelText

        acceptBtn.setOnClickListener {


            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {

            customCancelAction.cancelButton()

            dialog.dismiss()

        }

        dialog.show()

    }

}