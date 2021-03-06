package com.github.stephanenicolas.injectview;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * These tests are really complex to setup.
 * Take your time for maintenance.
 * @author SNI
 */
@RunWith(InjectViewTestRunner.class)
@Config(manifest= Config.NONE)
public class InjectViewProcessorForFragmentsWithOtherTest {
  public static final String VIEW_TAG = "TAG";
  public static final String VIEW_TAG2 = "TAG2";
  public static final int VIEW_ID = 101;
  public static final int VIEW_ID2 = 102;

  private InjectViewProcessor processor = new InjectViewProcessor();

  @Test
  public void shouldInjectFragment_withId() {
    TestActivityWithId activity = Robolectric.buildActivity(TestActivityWithId.class)
        .create()
        .get();
    assertNotNull(activity.pojoWithActivityConstructor.fragment);
    assertThat(activity.pojoWithActivityConstructor.fragment.getId(), is(VIEW_ID2));
    assertThat(activity.pojoWithActivityConstructor.fragment2.getTag(), is(VIEW_TAG2));
    assertNotNull(activity.pojoWithFragmentConstructor.fragment);
    assertThat(activity.pojoWithFragmentConstructor.fragment.getId(), is(VIEW_ID2));
    assertThat(activity.pojoWithActivityConstructor.fragment2.getTag(), is(VIEW_TAG2));
  }


  public static class TestActivityWithId extends Activity {
    private PojoWithActivityConstructor pojoWithActivityConstructor;
    private PojoWithFragmentConstructor pojoWithFragmentConstructor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      LinearLayout layout = new LinearLayout(this);
      final TextView text1 = new TextView(this);
      text1.setId(VIEW_ID);
      layout.addView(text1);
      LinearLayout layout2 = new LinearLayout(this);
      layout2.setId(VIEW_ID2);
      layout.addView(layout2);

      pojoWithActivityConstructor = new PojoWithActivityConstructor(this);
      setContentView(layout);
      pojoWithFragmentConstructor = new PojoWithFragmentConstructor(getFragmentManager().findFragmentByTag(
          VIEW_TAG2));
    }

    @Override
    public FragmentManager getFragmentManager() {
      FragmentManager fragmentManager = super.getFragmentManager();
      if (fragmentManager.findFragmentById(VIEW_ID2) == null ) {
        fragmentManager.beginTransaction().add(VIEW_ID2, new DummyFragment(), VIEW_TAG).commit();
      }
      if (fragmentManager.findFragmentByTag(VIEW_TAG2) == null ) {
        fragmentManager.beginTransaction().add(new DummyFragmentWithView(), VIEW_TAG2).commit();
      }
      fragmentManager.executePendingTransactions();
      return fragmentManager;
    }

    @Override
    public View findViewById(int id) {
      if (id == VIEW_ID) {
        final TextView text1 = new TextView(this);
        text1.setId(id);
        return text1;
      } else {
        return super.findViewById(id);
      }
    }
  }

  public static class PojoWithActivityConstructor {
    @InjectFragment(VIEW_ID2)
    protected Fragment fragment;
    @InjectFragment(tag = VIEW_TAG2)
    protected Fragment fragment2;

    public PojoWithActivityConstructor(Activity activity) {
    }
  }

  public static class PojoWithFragmentConstructor {
    @InjectFragment(VIEW_ID2)
    protected Fragment fragment;
    @InjectFragment(tag = VIEW_TAG2)
    protected Fragment fragment2;

    public PojoWithFragmentConstructor(Fragment fragment) {
    }
  }

  public static class DummyFragment extends Fragment {
    public DummyFragment() {
    }
  }

  public static class DummyFragmentWithView extends Fragment {
    @Override
    public View getView() {
      LinearLayout layout = new LinearLayout(getActivity());
      final TextView text1 = new TextView(getActivity());
      text1.setId(VIEW_ID);
      layout.addView(text1);
      return layout;
    }
  }
}
