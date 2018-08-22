package com.jiangzg.lovenote.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;

import java.util.Locale;

import butterknife.BindView;

public class UserProtocolActivity extends BaseActivity<UserProtocolActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tvContent)
    TextView tvContent;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, UserProtocolActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_user_protocol;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.user_protocol), true);
        String company = SPHelper.getCommonConst().getCompanyName();
        String appShort = getString(R.string.app_name);
        String app = "\"" + appShort + "\"";
        String companyApp = company + app;
        // 重要须知
        String importKnow = getString(R.string.agreement_import_know);
        String importKnowShow = String.format(Locale.getDefault(), importKnow, company, company, company, app, app);
        // 使用规则
        String useRule = "一、使用规则" +
                "\n1、用户充分了解并同意，%s仅为用户提供信息分享、传送及获取的平台，用户必须为自己注册帐号下的一切行为负责，包括您所传送的任何内容以及由此产生的任何结果。用户应对%s中的内容自行加以判断，并承担因使用内容而引起的所有风险，包括因对内容的正确性、完整性或实用性的依赖而产生的风险。%s无法且不会对因用户行为而导致的任何损失或损害承担责任。" +
                "\n2、用户在%s服务中或通过%s服务所传送的任何内容并不反映%s的观点或政策，%s对此不承担任何责任。" +
                "\n3、用户充分了解并同意，%s是一个基于用户关系网的点对点即时通讯产品，用户须对在%s上的注册信息的真实性、合法性、有效性承担全部责任，用户不得冒充他人；不得利用他人的名义传播任何信息；不得恶意使用注册帐号导致其他用户误认；否则%s有权立即停止提供服务，收回%s”帐号并由用户独自承担由此而产生的一切法律责任。" +
                "\n4、用户须对在%s上所传送信息的真实性、合法性、无害性、有效性等全权负责，与用户所传播的信息相关的任何法律责任由用户自行承担，与%s无关。" +
                "\n5、%s保留因业务发展需要，单方面对本服务的全部或部分服务内容在任何时候不经任何通知的情况下变更、暂停、限制、终止或撤销%s服务的权利，用户需承担此风险。" +
                "\n6、%s提供的服务中可能包括广告，用户同意在使用过程中显示%s和第三方供应商、合作伙伴提供的广告。" +
                "\n7、用户不得利用%s或%s服务制作、上载、复制、发送如下内容：" +
                "\n (1) 反对宪法所确定的基本原则的；" +
                "\n (2) 危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；" +
                "\n (3) 损害国家荣誉和利益的；" +
                "\n (4) 煽动民族仇恨、民族歧视，破坏民族团结的；" +
                "\n (5) 破坏国家宗教政策，宣扬邪教和封建迷信的；" +
                "\n (6) 散布谣言，扰乱社会秩序，破坏社会稳定的；" +
                "\n (7) 散布淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的；" +
                "\n (8) 侮辱或者诽谤他人，侵害他人合法权益的；" +
                "\n (9) 含有法律、行政法规禁止的其他内容的信息。" +
                "\n8、%s可依其合理判断，对违反有关法律法规或本协议约定；或侵犯、妨害、威胁任何人权利或安全的内容，或者假冒他人的行为，%s有权依法停止传输任何前述内容，并有权依其自行判断对违反本条款的任何人士采取适当的法律行动，包括但不限于，从%s服务中删除具有违法性、侵权性、不当性等内容，终止违反者的成员资格，阻止其使用%s全部或部分服务，并且依据法律法规保存有关信息并向有关部门报告等。" +
                "\n9、用户权利及义务：" +
                "\n (1) %s帐号的所有权归%s所有，用户完成申请注册手续后，获得%s帐号的使用权，该使用权仅属于初始申请注册人，禁止赠与、借用、租用、转让或售卖。%s因经营需要，有权回收用户的%s帐号。" +
                "\n (2) 用户有权更改、删除在%s上的个人资料、注册信息及传送内容等，但需注意，删除有关信息的同时也会删除任何您储存在系统中的文字和图片。用户需承担该风险。" +
                "\n (3) 用户有责任妥善保管注册帐号信息及帐号密码的安全，用户需要对注册帐号以及密码下的行为承担法律责任。用户同意在任何情况下不使用其他成员的帐号或密码。在您怀疑他人在使用您的帐号或密码时，您同意立即通知%s。" +
                "\n (4) 用户应遵守本协议的各项条款，正确、适当地使用本服务，如因用户违反本协议中的任何条款，%s有权依据协议终止对违约用户%s帐号提供服务。同时，%s保留在任何时候收回%s帐号、用户名的权利。" +
                "\n (5) 用户注册帐号后如果长期不登录该帐号，有权回收该帐号，以免造成资源浪费，由此带来问题均由用户自行承担。";
        String useRuleShow = String.format(Locale.getDefault(), useRule,
                companyApp, companyApp, company,
                companyApp, companyApp, company, company,
                companyApp, companyApp, company, companyApp,
                companyApp, company,
                company, companyApp,
                companyApp, companyApp,
                companyApp, companyApp,
                company, company, companyApp, companyApp,
                companyApp, company, companyApp, company, companyApp,
                companyApp,
                company,
                company, companyApp, company, companyApp,
                companyApp, company);
        // 隐私保护
        String privateProtect = "二、隐私保护" +
                "\n用户同意个人隐私信息是指那些能够对用户进行个人辨识或涉及个人通信的信息，包括下列信息：用户真实姓名，身份证号，手机号码，IP地址。而非个人隐私信息是指用户对本服务的操作状态以及使用习惯等一些明确且客观反映在%s服务器端的基本记录信息和其他一切个人隐私信息范围外的普通信息；以及用户同意公开的上述隐私信息；" +
                "\n尊重用户个人隐私信息的私有性是北%s的一贯制度，%s将会采取合理的措施保护用户的个人隐私信息，除法律或有法律赋予权限的政府部门要求或用户同意等原因外，%s未经用户同意不向除合作单位以外的第三方公开、 透露用户个人隐私信息。 但是，用户在注册时选择同意，或用户与%s及合作单位之间就用户个人隐私信息公开或使用另有约定的除外，同时用户应自行承担因此可能产生的任何风险，%s对此不予负责。同时，为了运营和改善%s的技术和服务，%s将可能会自行收集使用或向第三方提供用户的非个人隐私信息，这将有助于%s向用户提供更好的用户体验和提高%s的服务质量。" +
                "\n用户同意，在使用%s服务时也同样受%s隐私政策的约束。当您接受本协议条款时，您同样认可并接受%s隐私政策的条款。";
        String privateProtectShow = String.format(Locale.getDefault(), privateProtect, company,
                company, company, company, company, company, company, company, company, company,
                companyApp, company, company);
        // 商标信息
        String iconInfo = "三、%s商标信息" +
                "\n%s服务中所涉及的图形、文字或其组成，以及其他%s标志及产品、服务名称，均为%s之商标（以下简称“%s标识”）。未经%s事先书面同意，用户不得将%s标识以任何方式展示或使用或作其他处理，也不得向他人表明您有权展示、使用、或其他有权处理%s标识的行为。";
        String iconInfoShow = String.format(Locale.getDefault(), iconInfo, companyApp,
                companyApp, companyApp, companyApp, company, company, company, company);
        // 法律责任
        String lawResponse = "四、法律责任及免责" +
                "\n1、用户违反本《协议》或相关的服务条款的规定，导致或产生的任何第三方主张的任何索赔、要求或损失，包括合理的律师费，用户同意赔偿%s与合作公司、关联公司，并使之免受损害。" +
                "\n2、用户因第三方如电信部门的通讯线路故障、技术问题、网络、电脑故障、系统不稳定性及其他各种不可抗力原因而遭受的一切损失，%s及合作单位不承担责任。" +
                "\n3、因技术故障等不可抗事件影响到服务的正常运行的，%s及合作单位承诺在第一时间内与相关单位配合，及时处理进行修复，但用户因此而遭受的一切损失，%s及合作单位不承担责任。" +
                "\n4、本服务同大多数互联网服务一样，受包括但不限于用户原因、网络服务质量、社会环境等因素的差异影响，可能受到各种安全问题的侵扰，如他人利用用户的资料，造成现实生活中的骚扰；用户下载安装的其它软件或访问的其他网站中含有“特洛伊木马”等病毒，威胁到用户的计算机信息和数据的安全，继而影响本服务的正常使用等等。用户应加强信息安全及使用者资料的保护意识，要注意加强密码保护，以免遭致损失和骚扰。" +
                "\n5、用户须明白，使用本服务因涉及Internet服务，可能会受到各个环节不稳定因素的影响。因此，本服务存在因不可抗力、计算机病毒或黑客攻击、系统不稳定、用户所在位置、用户关机以及其他任何技术、互联网络、通信线路原因等造成的服务中断或不能满足用户要求的风险。用户须承担以上风险，%s不作担保。对因此导致用户不能发送和接受阅读信息、或接发错信息，%s不承担任何责任。" +
                "\n6、用户须明白，在使用本服务过程中存在有来自任何他人的包括威胁性的、诽谤性的、令人反感的或非法的内容或行为或对他人权利的侵犯（包括知识产权）的匿名或冒名的信息的风险，用户须承担以上风险，%s和合作公司对本服务不作任何类型的担保，不论是明确的或隐含的，包括所有有关信息真实性、适商性、适于某一特定用途、所有权和非侵权性的默示担保和条件，对因此导致任何因用户不正当或非法使用服务产生的直接、间接、偶然、特殊及后续的损害，不承担任何责任。" +
                "\n7、%s定义的信息内容包括：文字、软件、声音、相片、录像、图表；在广告中全部内容；%s为用户提供的商业信息，所有这些内容受版权、商标权、和其它知识产权和所有权法律的保护。所以，用户只能在%s和广告商授权下才能使用这些内容，而不能擅自复制、修改、编纂这些内容、或创造与内容有关的衍生产品。" +
                "\n8、在任何情况下，%s均不对任何间接性、后果性、惩罚性、偶然性、特殊性或刑罚性的损害，包括因用户使用%s服务而遭受的利润损失，承担责任（即使%s已被告知该等损失的可能性亦然）。尽管本协议中可能含有相悖的规定，%s对您承担的全部责任，无论因何原因或何种行为方式，始终不超过您在成员期内因使用%s服务而支付给%s的费用(如有) 。";
        String lawResponseShow = String.format(Locale.getDefault(), lawResponse, company, company, company, company, company, company, company,
                company, company, company, company, companyApp, companyApp, company, companyApp, company);
        // 社区管理
        String socialManage = "五、%s社区管理规则" +
                "\n%s是和现实相关的社交产品，希望用户相互尊重，遵循和现实社会一样的社交礼仪。" +
                "\n为避免遭到用户举报而被封禁设备，请您遵守以下原则：" +
                "\n1、请勿发送涉嫌性骚扰的文字、图片及语音信息；" +
                "\n2、请勿使用含色情、淫秽意味或其他令人不适的头像或资料；" +
                "\n3、请勿在交谈中使用辱骂、恐吓、威胁等言论；" +
                "\n4、请勿发布各类垃圾广告、恶意信息、诱骗信息；" +
                "\n5、请勿盗用他人头像或资料，请勿伪装他人身份；" +
                "\n6、请勿发布不当政治言论或者任何违反国家法规政策的言论。" +
                "如用户违反社区管理规则，%s有权依据协议终止对违约用户%s帐号提供服务。同时，%s保留在任何时候收回%s帐号的权力。";
        String socialManageShow = String.format(Locale.getDefault(), socialManage, app, app, company, companyApp, company, app);
        // 其他条款
        String otherClause = "六、其他条款" +
                "\n1、%s郑重提醒用户注意本《协议》中免除%s责任和加重用户义务的条款，请用户仔细阅读，自主考虑风险。未成年人应在法定监护人的陪同下阅读本《协议》。以上各项条款内容的最终解释权及修改权归%s所有。" +
                "\n2、本《协议》所定的任何条款的部分或全部无效者，不影响其它条款的效力。" +
                "\n3、本《协议》的版权由%s所有，%s保留一切解释和修改权利。";
        String otherClauseShow = String.format(Locale.getDefault(), otherClause, company, company, company, company, company);
        // 全程
        String allName = "《%s软件许可及服务协议》";
        String allNameShow = String.format(Locale.getDefault(), allName, appShort);
        // content
        String show = importKnowShow + "\n\n" + useRuleShow + "\n\n" + privateProtectShow + "\n\n" + iconInfoShow + "\n\n" + lawResponseShow + "\n\n" + socialManageShow + "\n\n" + otherClauseShow + "\n\n" + allNameShow;
        tvContent.setText(show);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
    }

}
